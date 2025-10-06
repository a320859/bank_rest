package com.example.bankcards.entity;

import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.LocalDate;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties("cards")
    private User owner;

    private double balance;
    private LocalDate validityPeriod;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "block_requested", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private boolean blockRequested;

    @JsonProperty("number")
    public String getMaskedNumber() throws Exception {
        String decrypted = CardEncryptor.decrypt(number);
        return "**** **** **** " + decrypted.substring(decrypted.length() - 4);
    }

    public Card() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
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

    public LocalDate getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(LocalDate validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public CardStatus getStatus() {
        return status;
    }

    public boolean isBlockRequested() {
        return blockRequested;
    }

    public void setBlockRequested(boolean blockRequested) {
        this.blockRequested = blockRequested;
    }
}
