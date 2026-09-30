package com.dakartechwallet.walletbackend.mapper;

import com.dakartechwallet.walletbackend.dto.CompteResponse;
import com.dakartechwallet.walletbackend.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public CompteResponse toCompteResponse(Account account) {
        return new CompteResponse(
                account.getId(),
                account.getUser().getTelephone(),
                account.getSolde(),
                account.getDevise(),
                account.getStatut().name()
        );
    }
}