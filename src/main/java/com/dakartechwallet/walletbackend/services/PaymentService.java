package com.dakartechwallet.walletbackend.services;

import com.dakartechwallet.walletbackend.dto.InitierPaiementRequest;
import com.dakartechwallet.walletbackend.dto.PaiementResponse;
import com.dakartechwallet.walletbackend.entity.Account;
import com.dakartechwallet.walletbackend.entity.Transaction;
import com.dakartechwallet.walletbackend.entity.User;
import com.dakartechwallet.walletbackend.enums.StatutCompte;
import com.dakartechwallet.walletbackend.enums.StatutTransaction;
import com.dakartechwallet.walletbackend.enums.TypeTransaction;
import com.dakartechwallet.walletbackend.exception.PinInvalideException;
import com.dakartechwallet.walletbackend.exception.PinNonDefiniException;
import com.dakartechwallet.walletbackend.exception.ResourceNotFoundException;
import com.dakartechwallet.walletbackend.repository.AccountRepository;
import com.dakartechwallet.walletbackend.repository.TransactionRepository;
import com.dakartechwallet.walletbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Endpoint appelé par l'app Prestation.
     * Gère l'idempotence : si une transaction SUCCES existe déjà pour ce serviceId,
     * on la retourne sans redébiter (évite les doublons en cas de retry réseau).
     * Si une transaction ECHEC existe, on retente normalement (mécanisme de compensation NB0).
     */
    @Transactional
    public PaiementResponse initierPaiement(InitierPaiementRequest request) {

        // 1. Verrou sur le compte AVANT tout : deux requêtes simultanées sont sérialisées,
        //    la seconde verra donc le succès de la première à l'étape 2.
        Account account = accountRepository.findByUserIdForUpdate(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable"));

        // 2. Idempotence : déjà payé pour ce serviceId ? On ne redébite pas.
        Optional<Transaction> dejaPaye = transactionRepository
                .findFirstByReferenceExterneAndTypeAndStatut(
                        request.serviceId(), TypeTransaction.PAIEMENT, StatutTransaction.SUCCES);
        if (dejaPaye.isPresent()) {
            Transaction t = dejaPaye.get();
            return new PaiementResponse(t.getId(), t.getStatut(), request.serviceId(), "Paiement déjà effectué");
        }

        // 3. PIN
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        verifierPin(user, request.pin());

        // 4. Règles de débit (les échecs sont tracés : compensation NB0)
        if (account.getStatut() != StatutCompte.ACTIF) {
            return enregistrerEchec(account, request, "Compte suspendu");
        }
        if (account.getSolde().compareTo(request.montant()) < 0) {
            return enregistrerEchec(account, request, "Solde insuffisant");
        }

        account.setSolde(account.getSolde().subtract(request.montant()));
        accountRepository.save(account);

        Transaction transaction = transactionRepository.save(Transaction.builder()
                .account(account)
                .type(TypeTransaction.PAIEMENT)
                .montant(request.montant())
                .statut(StatutTransaction.SUCCES)
                .referenceExterne(request.serviceId())
                .description("Paiement service " + request.serviceId())
                .build());

        return new PaiementResponse(transaction.getId(), StatutTransaction.SUCCES,
                request.serviceId(), "Paiement effectué avec succès");
    }

    private PaiementResponse enregistrerEchec(Account account, InitierPaiementRequest request, String motif) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .type(TypeTransaction.PAIEMENT)
                .montant(request.montant())
                .statut(StatutTransaction.ECHEC)
                .referenceExterne(request.serviceId())
                .description(motif)
                .build();

        transactionRepository.save(transaction);

        return new PaiementResponse(transaction.getId(), StatutTransaction.ECHEC, request.serviceId(), motif);
    }

    private void verifierPin(User user, String pinSaisi) {
        if (user.getPinHash() == null) {
            throw new PinNonDefiniException("Le PIN n'a pas encore été défini");
        }
        if (!passwordEncoder.matches(pinSaisi, user.getPinHash())) {
            throw new PinInvalideException("PIN incorrect");
        }
    }
}