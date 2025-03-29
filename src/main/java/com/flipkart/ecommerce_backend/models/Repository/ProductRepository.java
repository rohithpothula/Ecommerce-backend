package com.flipkart.ecommerce_backend.models.Repository;

import com.flipkart.ecommerce_backend.models.Product;
import java.util.List;

import com.flipkart.ecommerce_backend.models.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Page<Product> findByNameContaining(String keyword, Pageable pageable);

  Page<Product> findByCategory(String category, Pageable pageable);

  Page<Product> findByBrand(String brand, Pageable pageable);

  Page<Product> findByStatus(ProductStatus status, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
  Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price >= :minPrice AND p.price <= :maxPrice")
  Page<Product> findByCategoryAndPriceBetween(String category, Double minPrice, Double maxPrice, Pageable pageable);


  Page<Product> findByCategoryAndBrand(String category, String brand, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.category = :category AND p.brand = :brand AND p.price >= :minPrice AND p.price <= :maxPrice")
  Page<Product> findByCategoryAndBrandAndPriceBetween(String category, String brand, Double minPrice, Double maxPrice, Pageable pageable);
}
