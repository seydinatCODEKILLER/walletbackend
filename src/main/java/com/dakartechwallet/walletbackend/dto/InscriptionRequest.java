package com.dakartechwallet.walletbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record InscriptionRequest(

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Numéro de téléphone invalide")
        String telephone,

        @NotBlank(message = "L'email est obligatoire")
        @jakarta.validation.constraints.Email(message = "Email invalide")
        String email
) {}