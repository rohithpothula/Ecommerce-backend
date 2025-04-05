package com.flipkart.ecommerce_backend.repository;


import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByToken(String token);

    @Transactional
    int deleteByExpiryDateBefore(Instant now);

    Optional<PasswordResetToken> findByUserAndExpiryDateAfter(LocalUser user, Instant now);
}
