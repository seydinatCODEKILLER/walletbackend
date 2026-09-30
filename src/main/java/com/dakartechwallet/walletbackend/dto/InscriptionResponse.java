package com.dakartechwallet.walletbackend.dto;

public record InscriptionResponse(
        String message,
        String codeOtpTelephone,
        String codeOtpEmail
) {}