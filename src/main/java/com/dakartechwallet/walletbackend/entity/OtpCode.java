package com.dakartechwallet.walletbackend.entity;

import com.dakartechwallet.walletbackend.enums.TypeOtp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String destinataire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TypeOtp type;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(nullable = false)
    private boolean utilise;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;
}