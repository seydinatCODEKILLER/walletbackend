package com.dakartechwallet.walletbackend.dto;

public record AuthResponse(
        String token,
        String telephone,
        String type
) {}