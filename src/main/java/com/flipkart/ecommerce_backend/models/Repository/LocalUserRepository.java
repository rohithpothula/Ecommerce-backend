package com.flipkart.ecommerce_backend.models.Repository;

import com.flipkart.ecommerce_backend.models.LocalUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalUserRepository extends JpaRepository<LocalUser, Long> {

  Optional<LocalUser> findByUsernameIgnoreCase(String username);

  Optional<LocalUser> findByEmailIgnoreCase(String email);
}
