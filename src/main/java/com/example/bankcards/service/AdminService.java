package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.BankCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.dto.UserManagementRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BankCardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, BankCardRepository cardRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(this::convertToUserResponse);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        cardRepository.deleteAll(cardRepository.findAllByOwnerId(userId));
        userRepository.delete(user);
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRoles()
        );
    }

    @Transactional
    public UserResponse createUser(UserManagementRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("Username and password are required for user creation.");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User with username " + request.getUsername() + " already exists.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            Role role = Role.valueOf(request.getRole().toUpperCase());
            user.setRoles(Collections.singleton(role));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role not found or invalid: " + request.getRole());
        }

        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserManagementRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new RuntimeException("Username " + request.getUsername() + " is already taken.");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                Role newRole = Role.valueOf(request.getRole().toUpperCase());
                user.setRoles(Collections.singleton(newRole));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Role not found or invalid: " + request.getRole());
            }
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }
}