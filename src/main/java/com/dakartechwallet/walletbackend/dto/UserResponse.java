package com.dakartechwallet.walletbackend.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String telephone,
        String email,
        boolean telephoneVerifie,
        boolean emailVerifie,
        String statut
) {}