package com.dakartechwallet.walletbackend.repository;

import com.dakartechwallet.walletbackend.entity.Transaction;
import com.dakartechwallet.walletbackend.enums.StatutTransaction;
import com.dakartechwallet.walletbackend.enums.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByAccountIdOrderByDateCreationDesc(UUID accountId);

    Optional<Transaction> findFirstByReferenceExterneAndTypeAndStatut(
            String referenceExterne, TypeTransaction type, StatutTransaction statut);

    List<Transaction> findByStatutAndType(StatutTransaction statut, TypeTransaction type);
}