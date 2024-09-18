package com.flipkart.ecommerce_backend.models.Repository;

import org.springframework.data.repository.ListCrudRepository;

import com.flipkart.ecommerce_backend.models.Product;

public interface ProductRepository extends ListCrudRepository<Product, Long>{

}
