package com.dakartechwallet.walletbackend.services;

import com.dakartechwallet.walletbackend.dto.*;
import com.dakartechwallet.walletbackend.entity.Account;
import com.dakartechwallet.walletbackend.entity.Transaction;
import com.dakartechwallet.walletbackend.entity.User;
import com.dakartechwallet.walletbackend.enums.StatutCompte;
import com.dakartechwallet.walletbackend.enums.StatutTransaction;
import com.dakartechwallet.walletbackend.enums.TypeTransaction;
import com.dakartechwallet.walletbackend.exception.*;
import com.dakartechwallet.walletbackend.mapper.AccountMapper;
import com.dakartechwallet.walletbackend.mapper.TransactionMapper;
import com.dakartechwallet.walletbackend.repository.AccountRepository;
import com.dakartechwallet.walletbackend.repository.TransactionRepository;
import com.dakartechwallet.walletbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    public CompteResponse consulterSolde(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable"));
        return accountMapper.toCompteResponse(account);
    }

    public List<TransactionResponse> historiqueTransactions(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable"));
        List<Transaction> transactions =
                transactionRepository.findByAccountIdOrderByDateCreationDesc(account.getId());
        return transactionMapper.toTransactionResponseList(transactions);
    }

    @Transactional
    public TransactionResponse deposer(UUID userId, DepotRequest request) {
        Account account = accountRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable"));

        verifierCompteActif(account);

        account.setSolde(account.getSolde().add(request.montant()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(TypeTransaction.DEPOT)
                .montant(request.montant())
                .statut(StatutTransaction.SUCCES)
                .description("Dépôt sur compte")
                .build();

        transactionRepository.save(transaction);
        return transactionMapper.toTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse retirer(UUID userId, RetraitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        verifierPin(user, request.pin());

        Account account = accountRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable"));

        verifierCompteActif(account);
        verifierSoldeSuffisant(account, request.montant());

        account.setSolde(account.getSolde().subtract(request.montant()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(TypeTransaction.RETRAIT)
                .montant(request.montant())
                .statut(StatutTransaction.SUCCES)
                .description("Retrait du compte")
                .build();

        transactionRepository.save(transaction);
        return transactionMapper.toTransactionResponse(transaction);
    }

    private void verifierPin(User user, String pinSaisi) {
        if (user.getPinHash() == null) {
            throw new PinNonDefiniException("Le PIN n'a pas encore été défini pour cet utilisateur");
        }
        if (!passwordEncoder.matches(pinSaisi, user.getPinHash())) {
            throw new PinInvalideException("PIN incorrect");
        }
    }

    private void verifierCompteActif(Account account) {
        if (account.getStatut() != StatutCompte.ACTIF) {
            throw new CompteInactifException("Le compte est suspendu");
        }
    }

    private void verifierSoldeSuffisant(Account account, BigDecimal montant) {
        if (account.getSolde().compareTo(montant) < 0) {
            throw new SoldeInsuffisantException("Solde insuffisant");
        }
    }
}