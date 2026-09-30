package com.dakartechwallet.walletbackend.dto;

import com.dakartechwallet.walletbackend.enums.StatutTransaction;
import com.dakartechwallet.walletbackend.enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TypeTransaction type,
        BigDecimal montant,
        StatutTransaction statut,
        String referenceExterne,
        LocalDateTime dateCreation
) {}