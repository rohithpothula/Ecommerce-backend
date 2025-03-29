package com.flipkart.ecommerce_backend.api.controllers.product;

import com.flipkart.ecommerce_backend.Constants.AppConstants;
import com.flipkart.ecommerce_backend.api.models.GenericResponseBody;
import com.flipkart.ecommerce_backend.api.models.ProductCreateDto;
import com.flipkart.ecommerce_backend.api.models.ProductDto;
import com.flipkart.ecommerce_backend.api.models.ProductUpdateDto;
import com.flipkart.ecommerce_backend.models.ProductStatus;
import com.flipkart.ecommerce_backend.services.ProductService;
import java.util.Map;

import com.flipkart.ecommerce_backend.utils.ResponseUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/products")
public class ProductController {

  @Autowired private ProductService productService;

  @PostMapping("")
  public ResponseEntity<GenericResponseBody> addProduct(@Valid @NotBlank @RequestBody ProductCreateDto productCreateDto) {
    productService.addProduct(productCreateDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success("Product added successfully", null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDto> getProduct(@Valid @NotBlank @PathVariable Long id) {
    ProductDto product = productService.getProduct(id);
    return ResponseEntity.status(HttpStatus.OK).body(product);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponseBody> deleteProduct(@Valid @NotBlank @PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.success("Product deleted successfully", null));
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenericResponseBody> updateProduct(@Valid @NotBlank @PathVariable Long id, @Valid @RequestBody ProductUpdateDto productUpdateDto) {
    productService.updateProduct(id, productUpdateDto);
    return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success("Product updated successfully", null));
  }

  @GetMapping("/category/{category}")
  public ResponseEntity<GenericResponseBody> getProductsByCategory(
          @Valid @NotBlank @PathVariable String category,
          @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
          @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
          @RequestParam(value = "sortDir", defaultValue =  AppConstants.SORT_DIR, required = false) String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<ProductDto> products = productService.getProductsByCategory(category, pageable);

    GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products", products));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/brand/{brand}")
  public ResponseEntity<GenericResponseBody> getProductsByBrand(
          @Valid @NotBlank @PathVariable String brand,
          @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
          @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
          @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<ProductDto> products = productService.getProductsByBrand(brand, pageable);

    GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products", products));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<GenericResponseBody> getProductsByStatus(
          @Valid @NotBlank @PathVariable ProductStatus status,
          @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
          @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
          @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
        ) {
    Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    Page<ProductDto> products = productService.getProductsByStatus(status, pageable);
    GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products", products));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/price/{min}/{max}")
  public ResponseEntity<GenericResponseBody> getProductsByPriceRange(
          @Valid @NotBlank @PathVariable Double min,
          @Valid @NotBlank @PathVariable Double max,
          @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
          @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
          @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<ProductDto> products = productService.getProductsByPriceRange(min, max, pageable);
    GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products",products));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{category}/price/{min}/{max}")
  public ResponseEntity<GenericResponseBody> getProductsByCategoryAndPriceRange(
          @Valid @NotBlank @PathVariable String category,
          @Valid @NotBlank @PathVariable Double min,
          @Valid @NotBlank @PathVariable Double max,
          @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
          @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
          @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProductsByCategoryAndPriceRange(category, min, max, pageable)));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{category}/brand/{brand}")
  public ResponseEntity<GenericResponseBody> getProductsByCategoryAndBrand(
          @Valid @NotBlank @PathVariable String category,
          @Valid @NotBlank @PathVariable String brand,
          @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
          @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
          @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProductsByCategoryAndBrand(category, brand, pageable)));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{category}/brand/{brand}/price/{min}/{max}")
  public ResponseEntity<GenericResponseBody> getProductsByCategoryAndBrandAndPriceRange(
          @Valid @NotBlank @PathVariable String category,
          @Valid @NotBlank @PathVariable String brand,
          @Valid @NotBlank @PathVariable Double min,
          @Valid @NotBlank @PathVariable Double max,
          @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
          @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
          @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProductsByCategoryAndBrandAndPriceRange(category, brand, min, max, pageable)));
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<GenericResponseBody> getProducts(
      @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)
          int pageNumber,
      @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false)
          int pageSize,
      @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
      @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false)
          String sortDir) {
      Sort sort  = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
      Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

      GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProducts(pageable)));
      return ResponseEntity.ok(response);
  }

  @GetMapping("search/{keyword}")
  public ResponseEntity<GenericResponseBody> searchProductByKeyword(
          @Valid @NotBlank @PathVariable String keyword,
          @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
          @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
          @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
          @RequestParam(value = "sortDir", defaultValue =  AppConstants.SORT_DIR, required = false) String sortDir
  ) {
    Sort sort  = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    GenericResponseBody response = ResponseUtil.success("Products fetched successfully", Map.of("products", productService.getProductsByKeyword(keyword, pageable)));
    return ResponseEntity.ok(response);
  }
}
