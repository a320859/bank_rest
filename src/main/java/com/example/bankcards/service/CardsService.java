package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<?> getCards(UserDetails userDetails) {
        int userId = userRepository.findIdByUsername(userDetails.getUsername());

        return ResponseEntity.ok().body(cardRepository.getCards(userId));
    }

    public ResponseEntity<?> saveCard(CardDTO cardDTO, UserDetails userDetails) {
        String number = cardDTO.getNumber();
        if (!number.matches("\\d{16}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The card number must contain 16 digits");
        }

        try{
            number = CardEncryptor.encrypt(number);
            if (cardRepository.countOfCardsWithNumber(number) != 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Card with this number already exists");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to encrypt card number");
        }
        int ownerId = userRepository.findIdByUsername(userDetails.getUsername());
        int balance = cardDTO.getBalance();
        LocalDate validityPeriod = LocalDate.now().plusDays(1);
        String status = String.valueOf(CardStatus.ACTIVE);
        cardRepository.saveCard(number, ownerId, balance, validityPeriod, status);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> transfer(TransferDTO transferDTO, UserDetails userDetails) {
        String fromCard = transferDTO.getFromCard();
        String toCard = transferDTO.getToCard();
        int amount = transferDTO.getAmount();

        try {
            fromCard = CardEncryptor.encrypt(fromCard);
            toCard = CardEncryptor.encrypt(toCard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to encrypt card number");
        }

        int userIdFromCard = cardRepository.getUserIdByCardNumber(fromCard);
        int userIdToCard = cardRepository.getUserIdByCardNumber(toCard);

        int userId =  userRepository.findIdByUsername(userDetails.getUsername());

        if (userId == userIdFromCard && userId == userIdToCard) {
            int fromCardBalance = cardRepository.getBalance(fromCard);
            int toCardBalance = cardRepository.getBalance(toCard);
            cardRepository.editBalance(fromCardBalance - amount, fromCard);
            cardRepository.editBalance(toCardBalance + amount, toCard);
            return ResponseEntity.ok().body("success");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cards not found");
        }
    }

    private String getMaskedNumber(String number) {
        String decrypted;
        try {
            decrypted = CardEncryptor.decrypt(number);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка расшифровки номера карты", e);
        }
        return "**** **** **** " + decrypted.substring(decrypted.length() - 4);
    }
}
