package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.service.CardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardsController {
    private final CardsService cardsService;

    public CardsController(CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @GetMapping("/cards")
    public ResponseEntity<?> getCards(@AuthenticationPrincipal UserDetails userDetails){
        return cardsService.getCards(userDetails);
    }

    @PostMapping("/cards")
    public ResponseEntity<?> saveCard(@RequestBody CardDTO cardDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.saveCard(cardDTO, userDetails);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferDTO transferDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.transfer(transferDTO, userDetails);
    }
}
