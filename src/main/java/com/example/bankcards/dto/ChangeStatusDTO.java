package com.example.bankcards.dto;

import com.example.bankcards.util.CardStatus;

public class ChangeStatusDTO {
    private CardStatus newStatus;


    public CardStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(CardStatus newStatus) {
        this.newStatus = newStatus;
    }
}
