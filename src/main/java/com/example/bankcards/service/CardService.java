package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.bankcards.service.EncryptionService;
import org.springframework.stereotype.Service;
import com.example.bankcards.dto.TransferRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final BankCardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    public CardService(BankCardRepository cardRepository, UserRepository userRepository, EncryptionService encryptionService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    private String maskCardNumber(String plainNumber) {
        if (plainNumber == null || plainNumber.length() < 4) return "************";
        return "************" + plainNumber.substring(plainNumber.length() - 4);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found in DB: " + username));
    }

    public CardResponse createCard(CreateCardRequest request) {
        try {
            User owner = getCurrentUser();
            String encryptedNumber = encryptionService.encrypt(request.getCardNumber());

            Card card = new Card();
            card.setCardNumberEncrypted(encryptedNumber);
            card.setOwner(owner);
            card.setExpiryDate(request.getExpiryDate());
            card.setBalance(request.getBalance());
            card.setStatus(CardStatus.ACTIVE);

            Card savedCard = cardRepository.save(card);
            return convertToResponseDto(savedCard);
        } catch (Exception e) {
            throw new RuntimeException("Error during card number encryption: " + e.getMessage(), e);
        }
    }

    public List<CardResponse> getAllCardsForCurrentUser() {
        User owner = getCurrentUser();
        List<Card> cards = cardRepository.findAllByOwnerId(owner.getId());
        return cards.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public CardResponse updateCardStatus(Long cardId, CardStatus newStatus) {
        User currentUser = getCurrentUser();

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));

        if (!card.getOwner().equals(currentUser)) {
            throw new RuntimeException("Access denied: Card does not belong to the current user.");
        }

        card.setStatus(newStatus);
        Card updatedCard = cardRepository.save(card);

        return convertToResponseDto(updatedCard);
    }

    private CardResponse convertToResponseDto(Card card) {
        try {
            String plainNumber = encryptionService.decrypt(card.getCardNumberEncrypted());
            String maskedNumber = maskCardNumber(plainNumber);

            return new CardResponse(
                    card.getId(),
                    maskedNumber,
                    card.getExpiryDate(),
                    card.getStatus(),
                    card.getBalance()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error during card number decryption: " + e.getMessage(), e);
        }
    }

    private Optional<Card> findCardByPlainNumber(String plainNumber) throws Exception {
        List<Card> allCards = cardRepository.findAll();
        for (Card card : allCards) {
            String decryptedNumber = encryptionService.decrypt(card.getCardNumberEncrypted());
            if (decryptedNumber.equals(plainNumber)) {
                return Optional.of(card);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public String transferFunds(TransferRequest request) throws Exception {
        User currentUser = getCurrentUser();
        BigDecimal amount = request.getAmount();

        Card sourceCard = findCardByPlainNumber(request.getSourceCardNumber())
                .orElseThrow(() -> new RuntimeException("Source card not found."));

        Card destinationCard = findCardByPlainNumber(request.getDestinationCardNumber())
                .orElseThrow(() -> new RuntimeException("Destination card not found."));

        if (!sourceCard.getOwner().equals(currentUser)) {
            throw new RuntimeException("Access denied: You can only transfer funds from your own card.");
        }

        if (sourceCard.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance on the source card.");
        }

        if (sourceCard.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Source card is not active.");
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(amount));
        cardRepository.save(sourceCard);

        destinationCard.setBalance(destinationCard.getBalance().add(amount));
        cardRepository.save(destinationCard);

        return "Transfer successful: " + amount + " transferred from " +
                maskCardNumber(encryptionService.decrypt(sourceCard.getCardNumberEncrypted())) +
                " to " + maskCardNumber(encryptionService.decrypt(destinationCard.getCardNumberEncrypted()));
    }
}