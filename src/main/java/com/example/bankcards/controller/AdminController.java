package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import com.example.bankcards.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        return adminService.deleteCard(id, userDetails);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        return adminService.deleteUser(id, userDetails);
    }

    @GetMapping("/cards/blockRequests")
    public List<Card> getCardBlockRequests(@AuthenticationPrincipal UserDetails userDetails) {
        return adminService.getCardBlockRequests(userDetails);
    }
}
