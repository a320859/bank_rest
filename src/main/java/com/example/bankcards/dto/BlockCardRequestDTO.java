package com.example.bankcards.dto;

public class BlockCardRequestDTO {
    private int cardId;
    private String reason;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}