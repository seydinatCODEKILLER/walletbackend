package com.dakartechwallet.walletbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Le téléphone est obligatoire") String telephone,
        @NotBlank(message = "Le PIN est obligatoire") String pin
) {}