package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankCardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByOwnerId(Long ownerId, Pageable pageable);
    Optional<Card> findByIdAndOwnerId(Long cardId, Long ownerId);
    List<Card> findAllByOwnerId(Long ownerId);
}