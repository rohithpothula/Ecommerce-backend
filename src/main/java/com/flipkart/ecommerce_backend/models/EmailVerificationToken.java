package com.flipkart.ecommerce_backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID; // Assuming User ID is UUID

@Entity
@Table(name = "email_verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal ID for the token record

    @Column(nullable = false, unique = true)
    private String token; // The unique verification token string

    // Link to the user this token is for
    @ManyToOne(targetEntity = LocalUser.class, fetch = FetchType.EAGER) // Eager might be needed to get user easily
    @JoinColumn(nullable = false, name = "user_id")
    private LocalUser user;

    @Column(nullable = false)
    private Instant expiryDate; // When this token becomes invalid

    public EmailVerificationToken(String token, LocalUser user, Instant expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
