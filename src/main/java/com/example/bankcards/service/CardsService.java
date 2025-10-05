package com.example.bankcards.service;

import com.example.bankcards.dto.BlockCardRequestDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.ChangeStatusDTO;
import com.example.bankcards.dto.TransferDTO;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.RoleUser;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.time.LocalDate;


@Service
public class CardsService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardsService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public Page<Card> getCards(UserDetails userDetails, int page, int size) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleUser.ADMIN)))) {
            return cardRepository.findAll(PageRequest.of(page, size));
        }
        int userId = userRepository.findIdByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return cardRepository.getCards(userId, PageRequest.of(page, size));
    }


    public ResponseEntity<?> saveCard(CardDTO cardDTO, UserDetails userDetails) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only an admin can create cards");
        }
        String number = cardDTO.getNumber();
        if (!number.matches("\\d{16}")) {
            throw new InvalidCardNumberException("The card number must contain 16 digits");
        }

        try{
            number = CardEncryptor.encrypt(number);
        } catch (Exception e) {
            throw new CardEncryptionException("Failed to encrypt card number");
        }

        if (cardRepository.countOfCardsWithNumber(number) != 0) {
            throw new CardNumberAlreadyExistsException("Card with this number already exists");
        }

        int ownerId = userRepository.findIdByUsername(cardDTO.getOwnerUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

        int balance = cardDTO.getBalance();
        LocalDate validityPeriod = LocalDate.now().plusDays(10);
        String status = String.valueOf(CardStatus.ACTIVE);
        cardRepository.saveCard(number, ownerId, balance, validityPeriod, status);
        return ResponseEntity.ok().body("The card was successfully created");
    }

    @Transactional
    public ResponseEntity<?> transfer(TransferDTO transferDTO, UserDetails userDetails) {

        String fromCard = transferDTO.getFromCard();
        String toCard = transferDTO.getToCard();
        int amount = transferDTO.getAmount();

        try {
            fromCard = CardEncryptor.encrypt(fromCard);
            toCard = CardEncryptor.encrypt(toCard);
        } catch (Exception e) {
            throw new CardEncryptionException("Failed to encrypt card number");
        }

        if (!cardRepository.findStatusByNumber(fromCard)
                .orElseThrow(() -> new CardNotFoundException("Card not found")).equals("ACTIVE") ||
                !cardRepository.findStatusByNumber(toCard)
                        .orElseThrow(() -> new CardNotFoundException("Card not found")).equals("ACTIVE")) {
            throw new CardInactiveException("Card is inactive");
        }

        int userIdFromCard = cardRepository.getUserIdByCardNumber(fromCard)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        int userIdToCard = cardRepository.getUserIdByCardNumber(toCard)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        int userId = userRepository.findIdByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userId == userIdFromCard && userId == userIdToCard) {
            int fromCardBalance = cardRepository.getBalance(fromCard);
            int toCardBalance = cardRepository.getBalance(toCard);
            if (fromCardBalance < amount) {return ResponseEntity.status(HttpStatus.CONFLICT).body("Insufficient funds");}
            cardRepository.editBalance(fromCardBalance - amount, fromCard);
            cardRepository.editBalance(toCardBalance + amount, toCard);
            return ResponseEntity.ok().body("Transaction successful");
        } else {
            throw new CardNotFoundException("Cards not found");
        }
    }

    public ResponseEntity<?> changeStatus(int id, ChangeStatusDTO changeStatusDTO, UserDetails userDetails) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only an admin can change status");
        }
        cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Card not found"));
        cardRepository.changeStatus(changeStatusDTO.getNewStatus(), id);
        return ResponseEntity.ok("Status changed");
    }

    public ResponseEntity<?> requestBlock(BlockCardRequestDTO dto, UserDetails userDetails) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleUser.ADMIN)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only an user can request a card block");
        }

        int userId = userRepository.findIdByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Card card = cardRepository.findById(dto.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (card.getOwner().getId() != userId) {
            throw new CardNotFoundException("Card not found");
        }

        card.setBlockRequested(true);
        cardRepository.save(card);

        return ResponseEntity.ok("Block request sent to admin");
    }
}
