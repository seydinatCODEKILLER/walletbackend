package com.dakartechwallet.walletbackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record InitierPaiementRequest(

        @NotNull(message = "L'ID utilisateur est obligatoire")
        UUID userId,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
        BigDecimal montant,

        @NotBlank(message = "L'ID du service est obligatoire")
        String serviceId,

        @NotBlank(message = "Le PIN est obligatoire")
        String pin
) {}