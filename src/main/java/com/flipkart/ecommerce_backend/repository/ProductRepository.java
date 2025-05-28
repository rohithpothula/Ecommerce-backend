package com.flipkart.ecommerce_backend.repository;

import com.flipkart.ecommerce_backend.models.Product;
import com.flipkart.ecommerce_backend.models.ProductStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Import Param
// Import EntityGraph
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

  // Override findAll to exclude DELETED status by default and fetch variants
  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  @Override
  Page<Product> findAll(Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.name LIKE %:keyword% AND p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  Page<Product> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.category = :category AND p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  Page<Product> findByCategory(@Param("category") String category, Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.brand = :brand AND p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  Page<Product> findByBrand(@Param("brand") String brand, Pageable pageable);

  // This method inherently filters by a specific status, so excluding DELETED might be redundant
  // unless you want to prevent querying *for* DELETED status explicitly.
  // If querying for DELETED is never intended, keep the check. If it might be needed (e.g., admin
  // view), remove the check here.
  // Let's assume querying for DELETED is not intended for standard flows:
  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.status = :status AND p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  Page<Product> findByStatus(@Param("status") ProductStatus status, Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.price >= :minPrice AND p.price <= :maxPrice AND p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  Page<Product> findByPriceBetween(
      @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.category = :category AND p.price >= :minPrice AND p.price <= :maxPrice AND p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  Page<Product> findByCategoryAndPriceBetween(
      @Param("category") String category,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.category = :category AND p.brand = :brand AND p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  Page<Product> findByCategoryAndBrand(
      @Param("category") String category, @Param("brand") String brand, Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.category = :category AND p.brand = :brand AND p.price >= :minPrice AND p.price <= :maxPrice AND p.status != com.flipkart.ecommerce_backend.models.ProductStatus.DELETED")
  Page<Product> findByCategoryAndBrandAndPriceBetween(
      @Param("category") String category,
      @Param("brand") String brand,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      Pageable pageable);

  // Consider adding JOIN FETCH for findById if variants are always needed when fetching a single
  // product
  // @Query("SELECT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.id = :id")
  // Optional<Product> findByIdWithVariants(@Param("id") UUID id);
}
