package com.example.bankcards.dto;

import com.example.bankcards.util.CardStatus;

public class ChangeStatusDTO {
    private CardStatus newStatus;

    public ChangeStatusDTO(CardStatus newStatus) {
        this.newStatus = newStatus;
    }


    public ChangeStatusDTO() {
    }

    public ChangeStatusDTO(String blocked) {
    }

    public CardStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(CardStatus newStatus) {
        this.newStatus = newStatus;
    }
}