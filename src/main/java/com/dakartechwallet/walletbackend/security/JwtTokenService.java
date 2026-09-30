package com.dakartechwallet.walletbackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    private final JwtUtil jwtUtil;

    @Override
    public String genererToken(UUID userId, String telephone) {
        return jwtUtil.genererToken(userId, telephone);
    }
}