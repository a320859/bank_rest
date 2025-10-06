package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import com.example.bankcards.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin", description = "Administrative operations with cards and users")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            summary = "Delete card by id",
            description = "Only the admin can delete a card."
    )
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        return adminService.deleteCard(id, userDetails);
    }

    @Operation(
            summary = "Delete user by id",
            description = "Only the admin can delete the user."
    )
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        return adminService.deleteUser(id, userDetails);
    }

    @Operation(
            summary = "Get all cards that have block requests",
            description = "Only the admin can see all cards with blocking requests"
    )
    @GetMapping("/cards/blockRequests")
    public List<Card> getCardBlockRequests(@AuthenticationPrincipal UserDetails userDetails) {
        return adminService.getCardBlockRequests(userDetails);
    }
}
