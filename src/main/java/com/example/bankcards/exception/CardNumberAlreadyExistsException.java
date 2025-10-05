package com.example.bankcards.exception;

public class CardNumberAlreadyExistsException extends RuntimeException {
    public CardNumberAlreadyExistsException(String message) {
        super(message);
    }
}
