package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.RoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private CardRepository cardRepository;
    private UserRepository userRepository;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        adminService = new AdminService(cardRepository, userRepository);
    }
    

    @Test
    void deleteCard_asAdmin_success() {
        UserDetails admin = mock(UserDetails.class);
        when(admin.getAuthorities()).thenReturn((Collection)List.of(new SimpleGrantedAuthority(String.valueOf(RoleUser.ADMIN))));

        ResponseEntity<?> response = adminService.deleteCard(1, admin);

        assertEquals("The deletion was successful", response.getBody());
        verify(cardRepository, times(1)).deleteCardById(1);
    }

    @Test
    void deleteCard_asUser_forbidden() {
        UserDetails user = mock(UserDetails.class);
        when(user.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER))));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminService.deleteCard(1, user));

        assertEquals("Only an admin can delete cards", exception.getReason());
        verify(cardRepository, never()).deleteCardById(anyInt());
    }
    

    @Test
    void deleteUser_asAdmin_success() {
        UserDetails admin = mock(UserDetails.class);
        when(admin.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(String.valueOf(RoleUser.ADMIN))));

        ResponseEntity<?> response = adminService.deleteUser(2, admin);

        assertEquals("The deletion was successful", response.getBody());
        verify(userRepository, times(1)).deleteById(2);
    }

    @Test
    void deleteUser_asUser_forbidden() {
        UserDetails user = mock(UserDetails.class);
        when(user.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER))));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminService.deleteUser(2, user));

        assertEquals("Only an admin can delete users", exception.getReason());
        verify(userRepository, never()).deleteById(anyInt());
    }
    

    @Test
    void getCardBlockRequests_asAdmin_success() {
        UserDetails admin = mock(UserDetails.class);
        when(admin.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(String.valueOf(RoleUser.ADMIN))));

        List<Card> mockCards = List.of(new Card(), new Card());
        when(cardRepository.getCardBlockRequests()).thenReturn(mockCards);

        List<Card> result = adminService.getCardBlockRequests(admin);

        assertEquals(2, result.size());
        verify(cardRepository, times(1)).getCardBlockRequests();
    }

    @Test
    void getCardBlockRequests_asUser_forbidden() {
        UserDetails user = mock(UserDetails.class);
        when(user.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER))));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminService.getCardBlockRequests(user));

        assertEquals("Only an admin can view card blocking requests.", exception.getReason());
        verify(cardRepository, never()).getCardBlockRequests();
    }
}
