package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockCardRequestDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.ChangeStatusDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cards", description = "Endpoints for cards management")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class CardsController {
    private final CardsService cardsService;

    public CardsController(CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @Operation(
            summary = "Get all cards of the current user",
            description = "If you are logged in as an administrator, you will receive cards of all users"
    )
    @GetMapping("/cards")
    public Page<Card> getCards(@AuthenticationPrincipal UserDetails userDetails, @RequestParam int page, @RequestParam int size){
        return cardsService.getCards(userDetails, page, size);
    }

    @Operation(
            summary = "Create a new card",
            description = "Only the admin can create a card."
    )
    @PostMapping("/cards")
    public ResponseEntity<?> saveCard(@RequestBody CardDTO cardDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.saveCard(cardDTO, userDetails);
    }

    @Operation(
            summary = "Transfer money between your cards"
    )
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferDTO transferDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.transfer(transferDTO, userDetails);
    }

    @Operation(
            summary = "Change the card status",
            description = "Only the admin can change the card status"
    )
    @PatchMapping("/cards/{id}/changeStatus")
    public ResponseEntity<?> changeStatus(@PathVariable int id, @RequestBody ChangeStatusDTO changeStatusDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.changeStatus(id, changeStatusDTO, userDetails);
    }

    @Operation(
            summary = "Make a request to block the card",
            description = "Only an user can request a card block"
    )
    @PostMapping("/cards/block-request")
    public ResponseEntity<?> requestBlock(@RequestBody BlockCardRequestDTO dto,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return cardsService.requestBlock(dto, userDetails);
    }

}
