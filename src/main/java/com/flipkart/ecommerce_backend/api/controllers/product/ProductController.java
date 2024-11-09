package com.flipkart.ecommerce_backend.api.controllers.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.ecommerce_backend.Constants.AppConstants;
import com.flipkart.ecommerce_backend.models.Product;
import com.flipkart.ecommerce_backend.models.Repository.ProductRepository;
import com.flipkart.ecommerce_backend.services.ProductService;

@RestController
@RequestMapping("api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping
	public List<Product> getProducts(
			@RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
			@RequestParam(value = "sortBy",defaultValue = "name",required = false) String sortBy,
			@RequestParam(value = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir) {
//		pageNumber always starts from 0
		return productService.getProducts(pageSize, pageNumber,sortBy,sortDir);
	}	
	
	
	@GetMapping("search/{keyword}")
	public List<Product> searchProductByKeyword(@PathVariable String keyword){
		System.out.println("akdf");
		return productService.findByKeyword(keyword);
	}

}
