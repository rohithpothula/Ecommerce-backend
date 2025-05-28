package com.flipkart.ecommerce_backend.repository;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalUserRepository extends JpaRepository<LocalUser, UUID> {

  Optional<LocalUser> findById(UUID id);

  Optional<LocalUser> findByUsername(String username);

  Optional<LocalUser> findByEmail(String email);

  Optional<LocalUser> findByUsernameIgnoreCase(String username);

  Optional<LocalUser> findByEmailIgnoreCase(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Page<LocalUser> findByRolesContains(Role role, Pageable pageable);

  boolean existsByRolesContaining(Role role);
}
