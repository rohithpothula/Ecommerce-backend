package com.flipkart.ecommerce_backend.models.Repository;

import org.springframework.data.repository.ListCrudRepository;

import com.flipkart.ecommerce_backend.models.VerificationToken;

public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, Long>{

}
