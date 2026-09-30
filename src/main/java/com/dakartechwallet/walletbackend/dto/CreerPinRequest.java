package com.dakartechwallet.walletbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreerPinRequest(

        @NotBlank(message = "Le téléphone est obligatoire")
        String telephone,

        @NotBlank(message = "Le PIN est obligatoire")
        @Pattern(regexp = "^[0-9]{4,6}$", message = "Le PIN doit contenir entre 4 et 6 chiffres")
        String pin
) {}