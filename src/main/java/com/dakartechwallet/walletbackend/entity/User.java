package com.dakartechwallet.walletbackend.entity;

import com.dakartechwallet.walletbackend.enums.StatutUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "telephone"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String telephone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "pin_hash")
    private String pinHash;

    @Column(name = "telephone_verifie", nullable = false)
    private boolean telephoneVerifie;

    @Column(name = "email_verifie", nullable = false)
    private boolean emailVerifie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutUser statut;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Account account;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;
}