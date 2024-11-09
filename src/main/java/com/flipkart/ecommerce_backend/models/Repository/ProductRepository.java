package com.flipkart.ecommerce_backend.models.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import com.flipkart.ecommerce_backend.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	List<Product> findByNameContaining(String keyword);
	
	
	@Query("select p from Product p where p.name like :key%")
	List<Product> searchByName(@Param("key") String keyword);
	
}
