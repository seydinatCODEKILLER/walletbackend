package com.dakartechwallet.walletbackend.dto;

import com.dakartechwallet.walletbackend.enums.TypeOtp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifierOtpRequest(

        @NotBlank(message = "Le destinataire est obligatoire")
        String destinataire,

        @NotNull(message = "Le type d'OTP est obligatoire")
        TypeOtp type,

        @NotBlank(message = "Le code OTP est obligatoire")
        String code
) {}