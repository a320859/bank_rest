package com.example.bankcards.controller;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    private final UserDetails mockUserDetails =
            User.withUsername("admin").password("pass").roles("ADMIN").build();

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCard_ReturnsOk() throws Exception {
        given(adminService.deleteCard(eq(1), any(UserDetails.class)))
                .willAnswer(invocation -> ResponseEntity.ok("The deletion was successful"));

        mockMvc.perform(delete("/cards/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("The deletion was successful"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_ReturnsOk() throws Exception {
        given(adminService.deleteUser(eq(5), any(UserDetails.class)))
                .willAnswer(invocation ->ResponseEntity.ok("The deletion was successful"));

        mockMvc.perform(delete("/users/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("The deletion was successful"));
    }



    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCardBlockRequests_ReturnsList() throws Exception {

        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);

        List<Card> cards = List.of(card1, card2);

        given(adminService.getCardBlockRequests(any(UserDetails.class)))
                .willReturn(cards);

        mockMvc.perform(get("/cards/blockRequests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }
}

