package com.flipkart.ecommerce_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@ToString(exclude = {"addresses", "verificationTokens"})  // Add this annotation
@Table(name = "local_user")
public class LocalUser {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_name", nullable = false, unique = true)
  private String username;

  @JsonIgnore
  @Column(name = "password", nullable = false, length = 1000)
  private String password;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @JsonIgnore
  @OneToMany(mappedBy = "localUser", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Address> addresses = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "localUser", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id desc")
  private List<VerificationToken> verificationTokens = new ArrayList<>();

  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified = false;
}
