package com.flipkart.ecommerce_backend.repository;


import com.flipkart.ecommerce_backend.models.EmailVerificationToken;
import com.flipkart.ecommerce_backend.models.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByToken(String token);

    Optional<EmailVerificationToken> findByUser_Id(UUID userId); // Find by user ID

    void deleteByUser(LocalUser user);

    int deleteAllByExpiryDateBefore(Instant now);
}
