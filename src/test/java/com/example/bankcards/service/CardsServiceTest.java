package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.RoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardsServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    private CardsService cardsService;

    private final String TEST_USERNAME = "testuser";
    private final int TEST_USER_ID = 100;
    private final String PLAIN_CARD_FROM = "1111222233334444";
    private final String PLAIN_CARD_TO = "5555666677778888";
    private final String ENCRYPTED_CARD_FROM = "ENCRYPTED_1111222233334444";
    private final String ENCRYPTED_CARD_TO = "ENCRYPTED_5555666677778888";


    private UserDetails adminDetails;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        cardsService = new CardsService(cardRepository, userRepository);

        adminDetails = mock(UserDetails.class);
        lenient().when(adminDetails.getUsername()).thenReturn("admin");
        lenient().when(adminDetails.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(String.valueOf(RoleUser.ADMIN))));

        userDetails = mock(UserDetails.class);
        lenient().when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        lenient().when(userDetails.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(String.valueOf(RoleUser.USER))));
    }


    @Test
    void getCards_asAdmin_returnsAllCards() {
        Page<Card> mockPage = new PageImpl<>(List.of(new Card(), new Card()));
        when(cardRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);

        Page<Card> result = cardsService.getCards(adminDetails, 0, 10);

        assertEquals(2, result.getTotalElements());
        verify(cardRepository, times(1)).findAll(any(PageRequest.class));
    }
}