package com.example.bankcards.dto;

public class CardDTO {
    private String number;
    private int balance;
    private String ownerUsername;

    public String getNumber() {
        return number;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getBalance() {
        return balance;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
}
