package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockCardRequestDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.ChangeStatusDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardsService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class CardsController {
    private final CardsService cardsService;

    public CardsController(CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @GetMapping("/cards")
    public Page<Card> getCards(@AuthenticationPrincipal UserDetails userDetails, @RequestParam int page, @RequestParam int size){
        return cardsService.getCards(userDetails, page, size);
    }

    @PostMapping("/cards")
    public ResponseEntity<?> saveCard(@RequestBody CardDTO cardDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.saveCard(cardDTO, userDetails);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferDTO transferDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.transfer(transferDTO, userDetails);
    }

    @PostMapping("/cards/{id}/changeStatus")
    public ResponseEntity<?> changeStatus(@PathVariable int id, @RequestBody ChangeStatusDTO changeStatusDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.changeStatus(id, changeStatusDTO, userDetails);
    }

    @PostMapping("/cards/block-request")
    public ResponseEntity<?> requestBlock(@RequestBody BlockCardRequestDTO dto,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.requestBlock(dto, userDetails);
    }

}
