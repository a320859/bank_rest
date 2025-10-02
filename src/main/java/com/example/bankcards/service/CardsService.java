package com.example.bankcards.service;

import com.example.bankcards.dto.BlockCardRequestDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.ChangeStatusDTO;
import com.example.bankcards.dto.TransferDTO;

import com.example.bankcards.entity.Card;
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
        int userId = userRepository.findIdByUsername(userDetails.getUsername());
        return cardRepository.getCards(userId, PageRequest.of(page, size));
    }


    public ResponseEntity<?> saveCard(CardDTO cardDTO, UserDetails userDetails) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only an admin can create cards");
        }
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
        int ownerId = userRepository.findIdByUsername(cardDTO.getOwnerUsername());
        int balance = cardDTO.getBalance();
        LocalDate validityPeriod = LocalDate.now().plusDays(1);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to encrypt card number");
        }

        if (!cardRepository.findStatusByNumber(fromCard).equals("ACTIVE") ||
                !cardRepository.findStatusByNumber(toCard).equals("ACTIVE")){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Card is inactive");
        }

        int userIdFromCard = cardRepository.getUserIdByCardNumber(fromCard);
        int userIdToCard = cardRepository.getUserIdByCardNumber(toCard);
        int userId =  userRepository.findIdByUsername(userDetails.getUsername());

        if (userId == userIdFromCard && userId == userIdToCard) {
            int fromCardBalance = cardRepository.getBalance(fromCard);
            int toCardBalance = cardRepository.getBalance(toCard);
            if (fromCardBalance < amount) {return ResponseEntity.status(HttpStatus.CONFLICT).body("Insufficient funds");}
            cardRepository.editBalance(fromCardBalance - amount, fromCard);
            cardRepository.editBalance(toCardBalance + amount, toCard);
            return ResponseEntity.ok().body("Transaction successful");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cards not found");
        }
    }

    public ResponseEntity<?> changeStatus(int id, ChangeStatusDTO changeStatusDTO, UserDetails userDetails) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only an admin can change status");
        }
        cardRepository.changeStatus(changeStatusDTO.getNewStatus(), id);
        return ResponseEntity.ok("Status changed");
    }

    public ResponseEntity<?> requestBlock(BlockCardRequestDTO dto, UserDetails userDetails) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleUser.ADMIN)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only an user can request a card block");
        }

        int userId = userRepository.findIdByUsername(userDetails.getUsername());
        Card card = cardRepository.findById(dto.getCardId())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getOwner().getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot block someone else's card");
        }

        card.setBlockRequested(true);
        cardRepository.save(card);

        return ResponseEntity.ok("Block request sent to admin");
    }
}
