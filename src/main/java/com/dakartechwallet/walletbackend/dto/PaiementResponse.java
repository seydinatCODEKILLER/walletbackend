package com.dakartechwallet.walletbackend.dto;

import com.dakartechwallet.walletbackend.enums.StatutTransaction;

import java.util.UUID;

public record PaiementResponse(
        UUID transactionId,
        StatutTransaction statut,
        String serviceId,
        String message
) {}