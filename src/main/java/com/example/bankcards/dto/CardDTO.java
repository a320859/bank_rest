package com.example.bankcards.dto;

public class CardDTO {
    private String number;
    private int balance;

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
}
