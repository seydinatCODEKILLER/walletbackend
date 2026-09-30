package com.dakartechwallet.walletbackend.controller;

import com.dakartechwallet.walletbackend.dto.*;
import com.dakartechwallet.walletbackend.enums.TypeOtp;
import com.dakartechwallet.walletbackend.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription, OTP, PIN et connexion")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/inscription")
    public ResponseEntity<InscriptionResponse> inscrire(@Valid @RequestBody InscriptionRequest request) {
        return ResponseEntity.ok(authService.inscrire(request));
    }

    @PostMapping("/otp/demander")
    public ResponseEntity<OtpResponse> demanderOtp(
            @RequestParam String destinataire,
            @RequestParam TypeOtp type) {
        return ResponseEntity.ok(authService.demanderOtp(destinataire, type));
    }

    @PostMapping("/otp/verifier")
    public ResponseEntity<Void> verifierOtp(@Valid @RequestBody VerifierOtpRequest request) {
        authService.verifierOtp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pin/creer")
    public ResponseEntity<AuthResponse> creerPin(@Valid @RequestBody CreerPinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.creerPin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}