package com.flipkart.ecommerce_backend.models.Repository;

import com.flipkart.ecommerce_backend.models.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, Long> {}
