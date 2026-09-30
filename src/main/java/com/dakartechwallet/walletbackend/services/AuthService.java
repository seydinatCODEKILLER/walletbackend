package com.dakartechwallet.walletbackend.services;

import com.dakartechwallet.walletbackend.dto.*;
import com.dakartechwallet.walletbackend.entity.Account;
import com.dakartechwallet.walletbackend.entity.OtpCode;
import com.dakartechwallet.walletbackend.entity.User;
import com.dakartechwallet.walletbackend.enums.StatutUser;
import com.dakartechwallet.walletbackend.enums.TypeOtp;
import com.dakartechwallet.walletbackend.exception.*;
import com.dakartechwallet.walletbackend.repository.AccountRepository;
import com.dakartechwallet.walletbackend.repository.OtpCodeRepository;
import com.dakartechwallet.walletbackend.repository.UserRepository;
import com.dakartechwallet.walletbackend.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int OTP_LENGTH = 4;
    private static final int OTP_VALIDITE_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Étape 1 : inscription (téléphone + email). Crée l'utilisateur (en attente)
     * et son compte, puis génère un OTP pour le téléphone et un pour l'email.
     */
    @Transactional
    public InscriptionResponse inscrire(InscriptionRequest request) {
        User user = userRepository.findByTelephone(request.telephone()).orElse(null);

        if (user != null) {
            if (user.getStatut() == StatutUser.ACTIF) {
                throw new ConflitException("Un compte existe déjà pour ce numéro de téléphone");
            }
            // Inscription non terminée : on la reprend
            if (!request.email().equals(user.getEmail()) && userRepository.existsByEmail(request.email())) {
                throw new ConflitException("Cet email est déjà utilisé");
            }
            user.setEmail(request.email());
            user.setTelephoneVerifie(false);
            user.setEmailVerifie(false);
            userRepository.save(user);
        } else {
            if (userRepository.existsByEmail(request.email())) {
                throw new ConflitException("Cet email est déjà utilisé");
            }
            user = userRepository.save(User.builder()
                    .telephone(request.telephone())
                    .email(request.email())
                    .telephoneVerifie(false)
                    .emailVerifie(false)
                    .statut(StatutUser.EN_ATTENTE_VALIDATION)
                    .build());
            accountRepository.save(Account.builder().user(user).build());
        }

        String codeTel = creerOtp(request.telephone(), TypeOtp.TELEPHONE);
        String codeEmail = creerOtp(request.email(), TypeOtp.EMAIL);

        return new InscriptionResponse("Codes OTP envoyés (simulé)", codeTel, codeEmail);
    }

    /** Renvoi d'un OTP pour une inscription déjà démarrée. */
    @Transactional
    public OtpResponse demanderOtp(String destinataire, TypeOtp type) {
        boolean existe = (type == TypeOtp.TELEPHONE)
                ? userRepository.existsByTelephone(destinataire)
                : userRepository.existsByEmail(destinataire);
        if (!existe) {
            throw new ResourceNotFoundException("Aucune inscription en cours pour ce destinataire");
        }
        String code = creerOtp(destinataire, type);
        return new OtpResponse("Code OTP envoyé (simulé) à " + destinataire, code);
    }

    /** Étape 2 : vérification d'un OTP (à faire pour le téléphone ET l'email). */
    @Transactional
    public void verifierOtp(VerifierOtpRequest request) {
        OtpCode otp = otpCodeRepository
                .findTopByDestinataireAndTypeAndUtiliseFalseOrderByDateCreationDesc(
                        request.destinataire(), request.type())
                .orElseThrow(() -> new OtpInvalideException("Aucun code OTP actif trouvé"));

        if (otp.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new OtpInvalideException("Le code OTP a expiré");
        }
        if (!otp.getCode().equals(request.code())) {
            throw new OtpInvalideException("Code OTP incorrect");
        }

        otp.setUtilise(true);
        otpCodeRepository.save(otp);

        if (request.type() == TypeOtp.TELEPHONE) {
            User user = userRepository.findByTelephone(request.destinataire())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
            user.setTelephoneVerifie(true);
            userRepository.save(user);
        } else {
            User user = userRepository.findByEmail(request.destinataire())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
            user.setEmailVerifie(true);
            userRepository.save(user);
        }
    }

    /**
     * Étape 3 : création du PIN, possible uniquement si téléphone ET email sont vérifiés
     * et si aucun PIN n'existe encore. Retourne directement le JWT (cf. diagramme).
     */
    @Transactional
    public AuthResponse creerPin(CreerPinRequest request) {
        User user = userRepository.findByTelephone(request.telephone())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!user.isTelephoneVerifie() || !user.isEmailVerifie()) {
            throw new OtpInvalideException("Le téléphone et l'email doivent être vérifiés avant de définir le PIN");
        }
        if (user.getPinHash() != null) {
            throw new ConflitException("Le PIN est déjà défini pour ce compte");
        }

        user.setPinHash(passwordEncoder.encode(request.pin()));
        user.setStatut(StatutUser.ACTIF);
        userRepository.save(user);

        return new AuthResponse(tokenService.genererToken(user.getId(), user.getTelephone()), user.getTelephone(), "Bearer");
    }

    /** Connexion d'un utilisateur existant : téléphone + PIN. */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByTelephone(request.telephone())
                .orElseThrow(() -> new PinInvalideException("Téléphone ou PIN incorrect"));

        if (user.getPinHash() == null || !passwordEncoder.matches(request.pin(), user.getPinHash())) {
            throw new PinInvalideException("Téléphone ou PIN incorrect");
        }
        if (user.getStatut() != StatutUser.ACTIF) {
            throw new CompteInactifException("Le compte n'est pas actif");
        }

        return new AuthResponse(tokenService.genererToken(user.getId(), user.getTelephone()), user.getTelephone(), "Bearer");
    }

    // --- Méthodes privées ---

    private String creerOtp(String destinataire, TypeOtp type) {
        String code = genererCodeOtp();
        otpCodeRepository.save(OtpCode.builder()
                .destinataire(destinataire)
                .type(type)
                .code(code)
                .utilise(false)
                .dateExpiration(LocalDateTime.now().plusMinutes(OTP_VALIDITE_MINUTES))
                .build());
        return code;
    }

    private String genererCodeOtp() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}