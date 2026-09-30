package com.dakartechwallet.walletbackend.exception;

public class PinInvalideException extends RuntimeException {
    public PinInvalideException(String message) {
        super(message);
    }
}