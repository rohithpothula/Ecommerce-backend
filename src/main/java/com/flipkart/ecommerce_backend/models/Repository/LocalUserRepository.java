package com.flipkart.ecommerce_backend.models.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.flipkart.ecommerce_backend.models.LocalUser;

public interface LocalUserRepository extends CrudRepository<LocalUser, Long>{
	
	Optional<LocalUser> findByUsernameIgnoreCase(String username);
	Optional<LocalUser> findByEmailIgnoreCase(String email);
}
