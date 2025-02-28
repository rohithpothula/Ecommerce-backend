package com.flipkart.ecommerce_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "local_user")
public class LocalUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(name = "user_name", nullable = false, unique = true)
  private String username;

  @JsonIgnore
  @Column(name = "password", nullable = false, length = 1000)
  private String password;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "first_name", nullable = false)
  private String first_name;

  @Column(name = "last_name", nullable = false)
  private String last_name;

  @JsonIgnore
  @OneToMany(mappedBy = "id", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Address> address = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id desc")
  private List<VerificationToken> verificationTokens = new ArrayList<>();

  @Column(name = "email_verified", nullable = false)
  private boolean isEmailVerified = false;
}
