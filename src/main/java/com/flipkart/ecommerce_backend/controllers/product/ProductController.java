package com.flipkart.ecommerce_backend.controllers.product;

import com.flipkart.ecommerce_backend.dtos.GenericResponseBodyDto;
import com.flipkart.ecommerce_backend.dtos.ProductCreateDto;
import com.flipkart.ecommerce_backend.dtos.ProductDto;
import com.flipkart.ecommerce_backend.dtos.ProductUpdateDto;
import com.flipkart.ecommerce_backend.models.ProductStatus;
import com.flipkart.ecommerce_backend.services.ProductService;
import java.util.Map;
import java.util.UUID;

import com.flipkart.ecommerce_backend.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping()
  @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
  public ResponseEntity<GenericResponseBodyDto> addProduct(@Valid @RequestBody ProductCreateDto productCreateDto) {
    ProductDto saveProductDto = productService.addProduct(productCreateDto);
    GenericResponseBodyDto response = ResponseUtil.success("Product added successfully", Map.of("product", saveProductDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> getProduct(@PathVariable UUID id) {
    ProductDto product = productService.getProduct(id);
    GenericResponseBodyDto response = ResponseUtil.success("Product fetched successfully", Map.of("product", product));
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
  public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
    log.info("Request received to delete product ID: {}", id);

    productService.deleteProduct(id);

    log.info("Product deleted successfully: ID={}", id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
  public ResponseEntity<GenericResponseBodyDto> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductUpdateDto productUpdateDto) {
    ProductDto updatedProductDto = productService.updateProduct(id, productUpdateDto);
    GenericResponseBodyDto response = ResponseUtil.success("Product updated successfully", Map.of("product", updatedProductDto));
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/category/{category}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> getProductsByCategory(
          @PathVariable String category,
          Pageable pageable
  ) {
    Page<ProductDto> products = productService.getProductsByCategory(category, pageable);

    GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products", products));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/brand/{brand}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> getProductsByBrand(
          @PathVariable String brand,
          Pageable pageable
  ) {
    Page<ProductDto> products = productService.getProductsByBrand(brand, pageable);

    GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products", products));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/status/{status}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
  public ResponseEntity<GenericResponseBodyDto> getProductsByStatus(
          @PathVariable ProductStatus status,
          Pageable pageable
        ) {
    Page<ProductDto> products = productService.getProductsByStatus(status, pageable);
    GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products", products));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/price/{min}/{max}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> getProductsByPriceRange(
          @PathVariable Double min,
          @PathVariable Double max,
          Pageable pageable
  ) {
    Page<ProductDto> products = productService.getProductsByPriceRange(min, max, pageable);
    GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products",products));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{category}/price/{min}/{max}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> getProductsByCategoryAndPriceRange(
          @PathVariable String category,
          @PathVariable Double min,
          @PathVariable Double max,
          Pageable pageable
  ) {
    GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProductsByCategoryAndPriceRange(category, min, max, pageable)));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{category}/brand/{brand}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> getProductsByCategoryAndBrand(
          @PathVariable String category,
          @PathVariable String brand,
          Pageable pageable
  ) {
    GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProductsByCategoryAndBrand(category, brand, pageable)));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{category}/brand/{brand}/price/{min}/{max}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> getProductsByCategoryAndBrandAndPriceRange(
          @PathVariable String category,
          @PathVariable String brand,
          @PathVariable Double min,
          @PathVariable Double max,
          Pageable pageable
  ) {
    GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProductsByCategoryAndBrandAndPriceRange(category, brand, min, max, pageable)));
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> getProducts(
          Pageable pageable
          ) {

      GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProducts(pageable)));
      return ResponseEntity.ok(response);
  }

  @GetMapping("search/{keyword}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<GenericResponseBodyDto> searchProductByKeyword(
          @PathVariable String keyword,
          Pageable pageable
  ) {
    GenericResponseBodyDto response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProductsByKeyword(keyword, pageable)));
    return ResponseEntity.ok(response);
  }
}
