package com.example.bankcards.entity;

import com.example.bankcards.util.CardStatus;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String number;

    @ManyToOne
    @JoinColumn(name = "id")
    private User owner;

    private double balance;
    private Date validityPeriod;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Date validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public CardStatus getStatus() {
        return status;
    }
}
