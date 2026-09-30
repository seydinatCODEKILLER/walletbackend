package com.dakartechwallet.walletbackend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CompteResponse(
        UUID compteId,
        String telephone,
        BigDecimal solde,
        String devise,
        String statut
) {}