package com.example.bankcards.dto;

public class TransferDTO {
    private String fromCard;
    private String toCard;
    private int amount;

    public String getFromCard() {
        return fromCard;
    }

    public String getToCard() {
        return toCard;
    }

    public void setFromCard(String fromCard) {
        this.fromCard = fromCard;
    }

    public void setToCard(String toCard) {
        this.toCard = toCard;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
