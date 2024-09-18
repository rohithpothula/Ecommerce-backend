package com.flipkart.ecommerce_backend.api.controllers.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.ecommerce_backend.models.Product;
import com.flipkart.ecommerce_backend.models.Repository.ProductRepository;
import com.flipkart.ecommerce_backend.services.ProductService;

@RestController
@RequestMapping("api/products")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping
	public List<Product> getProducts(){
		return productService.getProducts();
	}

}
