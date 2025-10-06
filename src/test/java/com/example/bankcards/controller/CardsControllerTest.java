package com.example.bankcards.controller;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.BlockCardRequestDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.ChangeStatusDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardsService;
import com.example.bankcards.util.CardStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardsController.class)
@Import(TestSecurityConfig.class)
class CardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardsService cardsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getCards_ReturnsPage() throws Exception {

        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);
        List<Card> cardList = List.of(card1, card2);

        Page<Card> cardsPage = new PageImpl<>(cardList);

        given(cardsService.getCards(any(UserDetails.class), eq(0), eq(10)))
                .willReturn(cardsPage);

        mockMvc.perform(get("/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(cardsService).getCards(any(UserDetails.class), eq(0), eq(10));
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void saveCard_ReturnsOk() throws Exception {

        CardDTO cardDTO = new CardDTO("Savings", 500.0);
        String cardDTOJson = objectMapper.writeValueAsString(cardDTO);

        given(cardsService.saveCard(any(CardDTO.class), any(UserDetails.class)))
                .willReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardDTOJson))
                .andExpect(status().isCreated());
        verify(cardsService).saveCard(any(CardDTO.class), any(UserDetails.class));
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void transfer_ReturnsOk() throws Exception {

        TransferDTO transferDTO = new TransferDTO(1L, 2L, 100.0);
        String transferDTOJson = objectMapper.writeValueAsString(transferDTO);

        given(cardsService.transfer(any(TransferDTO.class), any(UserDetails.class)))
                .willReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferDTOJson))
                .andExpect(status().isOk());

        verify(cardsService).transfer(any(TransferDTO.class), any(UserDetails.class));
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void changeStatus_ReturnsOk() throws Exception {
        final int cardId = 123;

        ChangeStatusDTO changeStatusDTO = new ChangeStatusDTO(CardStatus.BLOCKED);
        String changeStatusDTOJson = objectMapper.writeValueAsString(changeStatusDTO);

        given(cardsService.changeStatus(eq(cardId), any(ChangeStatusDTO.class), any(UserDetails.class)))
                .willReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/cards/{id}/changeStatus", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changeStatusDTOJson))
                .andExpect(status().isOk());

        verify(cardsService).changeStatus(eq(cardId), any(ChangeStatusDTO.class), any(UserDetails.class));
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void requestBlock_ReturnsOk() throws Exception {

        BlockCardRequestDTO dto = new BlockCardRequestDTO(456L, "Lost card");
        String dtoJson = objectMapper.writeValueAsString(dto);

        given(cardsService.requestBlock(any(BlockCardRequestDTO.class), any(UserDetails.class)))
                .willReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/cards/block-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(cardsService).requestBlock(any(BlockCardRequestDTO.class), any(UserDetails.class));
    }
}