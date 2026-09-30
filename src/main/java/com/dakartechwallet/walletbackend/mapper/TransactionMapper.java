package com.dakartechwallet.walletbackend.mapper;

import com.dakartechwallet.walletbackend.dto.TransactionResponse;
import com.dakartechwallet.walletbackend.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public TransactionResponse toTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getMontant(),
                transaction.getStatut(),
                transaction.getReferenceExterne(),
                transaction.getDateCreation()
        );
    }

    public List<TransactionResponse> toTransactionResponseList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toTransactionResponse)
                .toList();
    }
}