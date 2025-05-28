package com.flipkart.ecommerce_backend.repository;

import com.flipkart.ecommerce_backend.models.PasswordResetToken;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<PasswordResetToken, Long> {
  PasswordResetToken findByToken(String token);

  void deleteByToken(String token);

  boolean existsByToken(String token);

  @Modifying
  @Transactional
  @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :now")
  int deleteExpiredTokens(@Param("now") Instant now);
}
