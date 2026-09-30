package com.dakartechwallet.walletbackend.security;

import java.util.UUID;

public interface TokenService {
    String genererToken(UUID userId, String telephone);
}