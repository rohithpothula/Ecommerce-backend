package com.flipkart.ecommerce_backend.api.controllers.product;

import com.flipkart.ecommerce_backend.Constants.AppConstants;
import com.flipkart.ecommerce_backend.models.Product;
import com.flipkart.ecommerce_backend.services.ProductService;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/products")
public class ProductController {

  @Autowired private ProductService productService;

  @Operation(
      summary = "Retrieve products",
      description = "Fetches all products. Requires Bearer token authentication.",
      security = { @SecurityRequirement(name = "bearer-key") }
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
          @ApiResponse(responseCode = "403", description = "User is not authorized to access this resource"),
  })
  @GetMapping
  public List<Product> getProducts(
      @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)
          int pageNumber,
      @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false)
          int pageSize,
      @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
      @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false)
          String sortDir) {
    //		pageNumber always starts from 0
    return productService.getProducts(pageSize, pageNumber, sortBy, sortDir);
  }

@Operation(
    summary = "Search products",
    description = "Searches for products by keyword. Requires Bearer token authentication.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "403", description = "User is not authorized to access this resource"),
})
  @GetMapping("search/{keyword}")
  public List<Product> searchProductByKeyword(@PathVariable String keyword) {
    System.out.println("akdf");
    return productService.findByKeyword(keyword);
  }
}
