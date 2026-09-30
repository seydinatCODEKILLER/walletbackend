package com.dakartechwallet.walletbackend.exception;

public class OtpInvalideException extends RuntimeException {
    public OtpInvalideException(String message) {
        super(message);
    }
}