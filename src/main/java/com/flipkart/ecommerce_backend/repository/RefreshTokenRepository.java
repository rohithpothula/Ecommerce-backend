package com.flipkart.ecommerce_backend.repository;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(LocalUser user); // For removing tokens on logout/password change

  void deleteByToken(String token);

  int deleteAllByExpiryDateBefore(Instant now); // For cleanup
}
