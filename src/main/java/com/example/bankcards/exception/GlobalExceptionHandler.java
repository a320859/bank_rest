package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleForbidden(ResponseStatusException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleCredentialsLogin(InvalidCredentialsException cred) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cred.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleRegister(UserAlreadyExistsException auth) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(auth.getMessage());
    }

    @ExceptionHandler(InvalidCardNumberException.class)
    public ResponseEntity<String> handleInvalidCardNumber(InvalidCardNumberException number) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(number.getMessage());
    }

    @ExceptionHandler(CardNumberAlreadyExistsException.class)
    public ResponseEntity<String> handleCardNumberAlreadyExists(CardNumberAlreadyExistsException number) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(number.getMessage());
    }

    @ExceptionHandler(CardEncryptionException.class)
    public ResponseEntity<String> handleCardEncryption(CardEncryptionException number) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(number.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException userNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userNotFoundException.getMessage());
    }

    @ExceptionHandler(CardInactiveException.class)
    public ResponseEntity<?> handleInactiveCard(CardInactiveException cardInactiveException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cardInactiveException.getMessage());
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<?> handleCardNotFound(CardNotFoundException cardNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cardNotFoundException.getMessage());
    }
}

