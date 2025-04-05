package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.dtos.ProductCreateDto;
import com.flipkart.ecommerce_backend.dtos.ProductDto;
import com.flipkart.ecommerce_backend.dtos.ProductVariantDto;
import com.flipkart.ecommerce_backend.exception.DatabaseException;
import com.flipkart.ecommerce_backend.exception.product.ProductException;
import com.flipkart.ecommerce_backend.exception.product.ProductNotFoundException;
import com.flipkart.ecommerce_backend.exception.product.SkuAlreadyExistsException;
import com.flipkart.ecommerce_backend.models.ProductVariant;
import com.flipkart.ecommerce_backend.dtos.ProductUpdateDto;
import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.models.Product;
import com.flipkart.ecommerce_backend.models.ProductStatus;
import com.flipkart.ecommerce_backend.repository.ProductRepository;

import com.flipkart.ecommerce_backend.repository.ProductVariantRepository;
import com.flipkart.ecommerce_backend.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductVariantRepository productVariantRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getProducts(Pageable pageable) {
    try {
      Page<Product> productPage = productRepository.findAll(pageable);
      return productPage.map(this::mapToDto);
    } catch (Exception e) {
      throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products");
    }
  }
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByKeyword(String keyword, Pageable pageable) {
    try {
       Page<Product> productPage = productRepository.findByNameContaining(keyword, pageable);
        return productPage.map(this::mapToDto);
    } catch (Exception e) {
      throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error searching products by keyword");
    }
  }

  @Override
  @Transactional
  public ProductDto addProduct(ProductCreateDto productCreateDto) {
      Product product = new Product();
      product.setName(productCreateDto.name());
      product.setShortDescription(productCreateDto.shortDescription());
      product.setLongDescription(productCreateDto.longDescription());
      product.setPrice(productCreateDto.variantPrice());
      product.setCategory(productCreateDto.category());
      product.setBrand(productCreateDto.brand());
      product.setImageUrl(productCreateDto.imageUrl());
      product.setStatus(ProductStatus.DRAFT);

      if (productVariantRepository.existsBySkuAndProductNotDeleted(productCreateDto.sku())) {
          log.warn("Product creation failed: SKU '{}' already exists.", productCreateDto.sku());
          throw new SkuAlreadyExistsException("SKU '" + productCreateDto.sku() + "' already exists.");
      }

      Product savedProduct;
      try {
          savedProduct = productRepository.save(product);
          log.debug("Saved base product with ID: {}", savedProduct.getId());
      } catch (DataIntegrityViolationException e) {
          log.error("Database error saving base product '{}': {}", productCreateDto.name(), e.getMessage(), e);
          throw new DatabaseException("Failed to save base product information.", e.getMessage());
      } catch (Exception e) {
          log.error("Unexpected error saving base product '{}': {}", productCreateDto.name(), e.getMessage(), e);
          throw new RuntimeException("An unexpected error occurred while saving the product.", e);
      }

      ProductVariant variant = new ProductVariant();
      variant.setProduct(savedProduct);
      variant.setSku(productCreateDto.sku());
      variant.setPrice(productCreateDto.variantPrice() != null ? productCreateDto.variantPrice() : savedProduct.getPrice());
      variant.setQuantity(productCreateDto.initialQuantity() != null ? productCreateDto.initialQuantity() : 0);
      variant.setColor(productCreateDto.color());
      variant.setSize(productCreateDto.size());
      variant.setImageUrl(productCreateDto.variantImageUrl());

      try {
          productVariantRepository.save(variant);
          log.debug("Saved initial variant with SKU: {}", variant.getSku());
      } catch (DataIntegrityViolationException e) {
          log.error("Database error saving product variant for product ID {}: {}", savedProduct.getId(), e.getMessage(), e);
          throw new DatabaseException("Failed to save product variant information.", e.getMessage());
      } catch (Exception e) {
          log.error("Unexpected error saving product variant for product ID {}: {}", savedProduct.getId(), e.getMessage(), e);
          throw new RuntimeException("An unexpected error occurred while saving the product variant.", e);
      }

      log.info("Successfully added product ID {} with initial variant SKU {}", savedProduct.getId(), variant.getSku());

      return mapToDto(savedProduct);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductDto getProduct(UUID id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    return mapToDto(product);
  }

  @Override
  @Transactional
  public void deleteProduct(UUID id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    product.setStatus(ProductStatus.DELETED);
    productRepository.save(product);
  }

  @Override
  @Transactional
  public ProductDto updateProduct(UUID id, ProductUpdateDto productUpdateDto) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
      try {
          updateProductEntity(product, productUpdateDto);
          product = productRepository.save(product);
          return mapToDto(product);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while updating product");
      }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByCategory(category, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by category");
      }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getProductsByBrand(String brand, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByBrand(brand, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by brand");
      }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getProductsByStatus(ProductStatus status, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByStatus(status, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by status");
      }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getProductsByPriceRange(Double min, Double max, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByPriceBetween(min, max, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by variantPrice range");
      }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getProductsByCategoryAndPriceRange(String category, Double min, Double max, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByCategoryAndPriceBetween(category, min, max, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by category and variantPrice range");
      }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getProductsByCategoryAndBrand(String category, String brand, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByCategoryAndBrand(category, brand, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by category and brand");
      }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getProductsByCategoryAndBrandAndPriceRange(String category, String brand, Double min, Double max, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByCategoryAndBrandAndPriceBetween(category, brand, min, max, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by category, brand and variantPrice range");
      }
  }


    private ProductVariantDto mapVariantToDto(ProductVariant variant) {
        if (variant == null) return null;
        return new ProductVariantDto(
                variant.getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getQuantity(),
                variant.getColor(),
                variant.getSize(),
                variant.getImageUrl()
        );
    }
  private ProductDto mapToDto(Product product) {
      List<ProductVariantDto> variantDtos = product.getVariants() == null ? Collections.emptyList() :
              product.getVariants().stream()
                      .map(this::mapVariantToDto)
                      .collect(Collectors.toList());
    return new ProductDto(
            product.getId(),
            product.getName(),
            product.getShortDescription(),
            product.getLongDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getBrand(),
            product.getImageUrl(),
            product.getStatus(),
            variantDtos
    );
  }

  private void updateProductEntity(Product product, ProductUpdateDto productUpdateDto) {
    product.setShortDescription(productUpdateDto.shortDescription());
    product.setLongDescription(productUpdateDto.longDescription());
    product.setPrice(productUpdateDto.price());
    product.setCategory(productUpdateDto.category());
    product.setBrand(productUpdateDto.brand());
    product.setImageUrl(productUpdateDto.imageUrl());
  }
}
