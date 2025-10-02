package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.RoleUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public AdminService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> deleteCard(int id, UserDetails userDetails) {
        forbidIfAdmin(userDetails, "Only an admin can delete cards");
        cardRepository.deleteCardById(id);
        return ResponseEntity.ok("The deletion was successful");
    }

    public ResponseEntity<?> deleteUser(int id, UserDetails userDetails) {
        forbidIfAdmin(userDetails, "Only an admin can delete users");
        userRepository.deleteById(id);
        return ResponseEntity.ok("The deletion was successful");
    }

    public List<Card> getCardBlockRequests(UserDetails userDetails) {
        forbidIfAdmin(userDetails, "Only an admin can view card blocking requests.");
        return cardRepository.getCardBlockRequests();
    }

    private void forbidIfAdmin(UserDetails userDetails, String message) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER)))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

}
