package com.flipkart.ecommerce_backend.models;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String token; // The actual unique token string

  @ManyToOne(
      targetEntity = LocalUser.class,
      fetch = FetchType.EAGER) // Eager fetch might be ok here
  @JoinColumn(nullable = false, name = "user_id")
  private LocalUser user;

  @Column(nullable = false)
  private Instant expiryDate;

  public PasswordResetToken(String token, LocalUser user, Instant expiryDate) {
    this.token = token;
    this.user = user;
    this.expiryDate = expiryDate;
  }

  public boolean isExpired() {
    return expiryDate.isBefore(Instant.now());
  }
}
