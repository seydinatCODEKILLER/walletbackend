package com.dakartechwallet.walletbackend.repository;

import com.dakartechwallet.walletbackend.entity.OtpCode;
import com.dakartechwallet.walletbackend.enums.TypeOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {

    Optional<OtpCode> findTopByDestinataireAndTypeAndUtiliseFalseOrderByDateCreationDesc(
            String destinataire, TypeOtp type);
}