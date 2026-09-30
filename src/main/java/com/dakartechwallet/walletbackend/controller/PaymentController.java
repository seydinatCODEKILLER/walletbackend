package com.dakartechwallet.walletbackend.controller;

import com.dakartechwallet.walletbackend.dto.InitierPaiementRequest;
import com.dakartechwallet.walletbackend.dto.PaiementResponse;
import com.dakartechwallet.walletbackend.services.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "Endpoint inter-microservices appelé par l'app Prestation")
@SecurityRequirement(name = "internalApiKey")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initier")
    public ResponseEntity<PaiementResponse> initierPaiement(
            @Valid @RequestBody InitierPaiementRequest request) {
        PaiementResponse response = paymentService.initierPaiement(request);
        return ResponseEntity.ok(response);
    }
}