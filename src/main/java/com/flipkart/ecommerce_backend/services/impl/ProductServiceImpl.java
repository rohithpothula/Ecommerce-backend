package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.Exception.BusinessException;
import com.flipkart.ecommerce_backend.Exception.ProductException;
import com.flipkart.ecommerce_backend.Exception.ProductNotFoundException;
import com.flipkart.ecommerce_backend.api.models.ProductCreateDto;
import com.flipkart.ecommerce_backend.api.models.ProductDto;
import com.flipkart.ecommerce_backend.api.models.ProductUpdateDto;
import com.flipkart.ecommerce_backend.hander.ErrorCode;
import com.flipkart.ecommerce_backend.models.Product;
import com.flipkart.ecommerce_backend.models.ProductStatus;
import com.flipkart.ecommerce_backend.models.Repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

import com.flipkart.ecommerce_backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

  @Autowired private ProductRepository productRepository;

  @Override
  public Page<ProductDto> getProducts(Pageable pageable) {
    try {
      Page<Product> productPage = productRepository.findAll(pageable);
      return productPage.map(this::mapToDto);
    } catch (Exception e) {
      throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products");
    }
  }

    @Override
    public Page<ProductDto> getProductsByKeyword(String keyword, Pageable pageable) {
        try {
            Page<Product> productPage = findByKeyword(keyword, pageable);
            return productPage.map(this::mapToDto);
        } catch (Exception e) {
            throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by keyword");
        }
    }

    public Page<Product> findByKeyword(String keyword, Pageable pageable) {
    try {
      return productRepository.findByNameContaining(keyword, pageable);
    } catch (Exception e) {
      throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error searching products by keyword");
    }
  }

  @Override
  public void addProduct(ProductCreateDto productCreateDto) {
      try {
          Product product = mapToEntity(productCreateDto);
          product.setStatus(ProductStatus.ACTIVE);
          productRepository.save(product);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while adding product");
      }
  }

  @Override
  public ProductDto getProduct(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    return mapToDto(product);
  }

  @Override
  public void deleteProduct(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    product.setStatus(ProductStatus.DELETED);
    productRepository.save(product);
  }

  @Override
  public void updateProduct(Long id, ProductUpdateDto productUpdateDto) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
      try {
          updateProductEntity(product, productUpdateDto);
          productRepository.save(product);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while updating product");
      }
  }

  @Override
  public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByCategory(category, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by category");
      }
  }

  @Override
  public Page<ProductDto> getProductsByBrand(String brand, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByBrand(brand, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by brand");
      }
  }

  @Override
  public Page<ProductDto> getProductsByStatus(ProductStatus status, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByStatus(status, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by status");
      }
  }

  @Override
  public Page<ProductDto> getProductsByPriceRange(Double min, Double max, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByPriceBetween(min, max, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by price range");
      }
  }

  @Override
  public Page<ProductDto> getProductsByCategoryAndPriceRange(String category, Double min, Double max, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByCategoryAndPriceBetween(category, min, max, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by category and price range");
      }
  }

  @Override
  public Page<ProductDto> getProductsByCategoryAndBrand(String category, String brand, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByCategoryAndBrand(category, brand, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by category and brand");
      }
  }

  @Override
  public Page<ProductDto> getProductsByCategoryAndBrandAndPriceRange(String category, String brand, Double min, Double max, Pageable pageable) {
      try {
          Page<Product> productPage = productRepository.findByCategoryAndBrandAndPriceBetween(category, brand, min, max, pageable);
          return productPage.map(this::mapToDto);
      } catch (Exception e) {
          throw new ProductException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching products by category, brand and price range");
      }
  }

  private Product mapToEntity(ProductCreateDto productCreateDto) {
    Product product = new Product();
    product.setName(productCreateDto.name());
    product.setShortDescription(productCreateDto.shortDescription());
    product.setLongDescription(productCreateDto.longDescription());
    product.setPrice(productCreateDto.price());
    product.setCategory(productCreateDto.category());
    product.setBrand(productCreateDto.brand());
    product.setImageUrl(productCreateDto.imageUrl());
    return product;
  }

  private ProductDto mapToDto(Product product) {
    return new ProductDto(
            product.getId(),
            product.getName(),
            product.getShortDescription(),
            product.getLongDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getBrand(),
            product.getImageUrl(),
            product.getStatus().toString()
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
