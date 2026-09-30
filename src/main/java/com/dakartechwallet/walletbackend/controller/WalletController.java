package com.dakartechwallet.walletbackend.controller;

import com.dakartechwallet.walletbackend.dto.*;
import com.dakartechwallet.walletbackend.entity.User;
import com.dakartechwallet.walletbackend.exception.ResourceNotFoundException;
import com.dakartechwallet.walletbackend.repository.UserRepository;
import com.dakartechwallet.walletbackend.services.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "Opérations sur le compte : solde, dépôt, retrait, historique")
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    @GetMapping("/solde")
    public ResponseEntity<CompteResponse> consulterSolde(Authentication authentication) {
        UUID userId = resoudreUserId(authentication);
        return ResponseEntity.ok(walletService.consulterSolde(userId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> historiqueTransactions(Authentication authentication) {
        UUID userId = resoudreUserId(authentication);
        return ResponseEntity.ok(walletService.historiqueTransactions(userId));
    }

    @PostMapping("/depot")
    public ResponseEntity<TransactionResponse> deposer(
            Authentication authentication,
            @Valid @RequestBody DepotRequest request) {
        UUID userId = resoudreUserId(authentication);
        return ResponseEntity.ok(walletService.deposer(userId, request));
    }

    @PostMapping("/retrait")
    public ResponseEntity<TransactionResponse> retirer(
            Authentication authentication,
            @Valid @RequestBody RetraitRequest request) {
        UUID userId = resoudreUserId(authentication);
        return ResponseEntity.ok(walletService.retirer(userId, request));
    }

    private UUID resoudreUserId(Authentication authentication) {
        String telephone = authentication.getName();
        User user = userRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return user.getId();
    }
}