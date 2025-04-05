package com.flipkart.ecommerce_backend.repository;

import com.flipkart.ecommerce_backend.models.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, Long> {}
