package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.AdminCreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.bankcards.dto.TransferRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

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

    private Card findCardAndVerifyOwner(Long cardId) {
        User currentUser = getCurrentUser();

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));

        if (!card.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("Access denied: Card does not belong to the current user.");
        }
        return card;
    }

    public CardResponse getCardByIdForCurrentUser(Long cardId) {
        Card card = findCardAndVerifyOwner(cardId);
        return convertToResponseDto(card);
    }

    public BalanceResponse getCardBalanceForCurrentUser(Long cardId) {
        Card card = findCardAndVerifyOwner(cardId);

        try {
            String plainNumber = encryptionService.decrypt(card.getCardNumberEncrypted());

            BalanceResponse response = new BalanceResponse();
            response.setBalance(card.getBalance());
            response.setMaskedCardNumber(maskCardNumber(plainNumber));

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching card balance: " + e.getMessage(), e);
        }
    }

    public Page<CardResponse> getAllCards(Pageable pageable) {
        Page<Card> cardsPage = cardRepository.findAll(pageable);
        return cardsPage.map(this::convertToResponseDto);
    }

    public CardResponse createCardForUser(AdminCreateCardRequest request) {
        try {
            User owner = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

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
            throw new RuntimeException("Error during card creation: " + e.getMessage(), e);
        }
    }

    public Page<CardResponse> getAllCardsForCurrentUser(Pageable pageable) {
        User owner = getCurrentUser();
        Page<Card> cardsPage = cardRepository.findAllByOwnerId(owner.getId(), pageable);
        return cardsPage.map(this::convertToResponseDto);
    }

    public CardResponse updateCardStatus(Long cardId, CardStatus newStatus) {
        Card card = findCardAndVerifyOwner(cardId);

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

    public void deleteCardById(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new RuntimeException("Card not found with ID: " + cardId);
        }
        cardRepository.deleteById(cardId);
    }

    public CardResponse adminUpdateCardStatus(Long cardId, CardStatus newStatus) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));
        card.setStatus(newStatus);
        Card updatedCard = cardRepository.save(card);

        return convertToResponseDto(updatedCard);
    }
}