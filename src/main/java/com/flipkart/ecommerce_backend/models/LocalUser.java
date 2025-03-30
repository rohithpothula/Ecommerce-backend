package com.flipkart.ecommerce_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@ToString(exclude = {"addresses", "verificationTokens"})  // Add this annotation
@Table(name = "local_user")
public class LocalUser implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
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

  // --- UserDetails Status Fields ---
  // These fields correspond to the UserDetails methods below.
  // Default values are set to common initial states.

  @Column(nullable = false)
  private boolean enabled = true; // Is the user account active?

  @Column(nullable = false)
  private boolean accountNonExpired = true; // Has the account itself expired?

  @Column(nullable = false)
  private boolean credentialsNonExpired = true; // Has the password expired?

  @Column(nullable = false)
  private boolean accountNonLocked = true; // Is the account locked (e.g., too many failed logins)?

  // --- Relationship with Role ---
  @ManyToMany(fetch = FetchType.EAGER) // EAGER fetch for roles is often needed for immediate authority checks
  @JoinTable(name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  // --- Relationship with Address ---
  @JsonIgnore
  @OneToMany(mappedBy = "localUser", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Address> addresses = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "localUser", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id desc")
  private List<VerificationToken> verificationTokens = new ArrayList<>();

  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified = false;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // ... null check ...
    return this.roles.stream()
            .map(role -> {
              String authorityString = role.getName().name(); // <-- BREAKPOINT 1: Inspect authorityString
              SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityString); // <-- BREAKPOINT 2: Inspect authority object
              return authority;
            })
            .collect(Collectors.toList());
  }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    @Override

    public boolean isEnabled() {
        return enabled;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }


  }
