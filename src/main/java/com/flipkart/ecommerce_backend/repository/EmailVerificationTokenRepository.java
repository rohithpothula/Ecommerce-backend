package com.flipkart.ecommerce_backend.repository;

import com.flipkart.ecommerce_backend.models.EmailVerificationToken;
import com.flipkart.ecommerce_backend.models.LocalUser;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationTokenRepository
    extends JpaRepository<EmailVerificationToken, Long> {

  Optional<EmailVerificationToken> findByToken(String token);

  void deleteByToken(String token);

  Optional<EmailVerificationToken> findByUser_Id(UUID userId); // Find by user ID

  void deleteByUser(LocalUser user);

  int deleteAllByExpiryDateBefore(Instant now);
}
