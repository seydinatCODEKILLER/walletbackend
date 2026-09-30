package com.dakartechwallet.walletbackend.exception;

public class CompteInactifException extends RuntimeException {
    public CompteInactifException(String message) {
        super(message);
    }
}