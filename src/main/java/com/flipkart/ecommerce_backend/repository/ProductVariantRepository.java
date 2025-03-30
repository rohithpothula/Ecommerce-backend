package com.flipkart.ecommerce_backend.repository;


import com.flipkart.ecommerce_backend.models.ProductVariant;
import com.flipkart.ecommerce_backend.models.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    // In ProductVariantRepository.java
    @Query("SELECT CASE WHEN COUNT(pv) > 0 THEN TRUE ELSE FALSE END " +
            "FROM ProductVariant pv JOIN pv.product p " +
            "WHERE pv.sku = :sku AND p.status <> ProductStatus.DELETED")
    boolean existsBySkuAndProductNotDeleted(@Param("sku") String sku);

    List<ProductVariant> findByProduct_Id(UUID productId);

    Optional<ProductVariant> findByProduct_IdAndColorAndSize(UUID productId, String color, String size);
}
