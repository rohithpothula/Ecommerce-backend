package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.api.models.ProductCreateDto;
import com.flipkart.ecommerce_backend.api.models.ProductDto;
import com.flipkart.ecommerce_backend.api.models.ProductUpdateDto;
import com.flipkart.ecommerce_backend.models.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductService {
    void addProduct(ProductCreateDto productCreateDto);

    ProductDto getProduct(Long id);

    void deleteProduct(Long id);

    void updateProduct(Long id, ProductUpdateDto productUpdateDto);

    Page<ProductDto> getProducts(Pageable pageable);

    Page<ProductDto> getProductsByKeyword(String keyword, Pageable pageable);

    Page<ProductDto> getProductsByCategory(String category, Pageable pageable);

    Page<ProductDto> getProductsByBrand(String brand, Pageable pageable);

    Page<ProductDto> getProductsByStatus(ProductStatus status, Pageable pageable);

    Page<ProductDto> getProductsByPriceRange(Double min, Double max, Pageable pageable);

    Page<ProductDto> getProductsByCategoryAndPriceRange(String category, Double min, Double max, Pageable pageable);

    Page<ProductDto> getProductsByCategoryAndBrand(String category, String brand, Pageable pageable);

    Page<ProductDto> getProductsByCategoryAndBrandAndPriceRange(String category, String brand, Double min, Double max, Pageable pageable);

}
