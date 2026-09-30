package com.dakartechwallet.walletbackend.dto;

public record OtpResponse(
        String message,
        String codeSimule
) {}