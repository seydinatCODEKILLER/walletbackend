package com.dakartechwallet.walletbackend.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String erreur,
        String message,
        String chemin
) {}