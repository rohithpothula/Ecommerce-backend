package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.models.Product;
import com.flipkart.ecommerce_backend.models.Repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  @Autowired private ProductRepository productRepository;

  public List<Product> getProducts(int pageSize, int pageNumber, String sortBy, String sortDir) {
    Sort sort = null;
    if (sortDir.equals("ASC")) {
      sort = Sort.by(sortBy).ascending();
    } else {
      sort = Sort.by(sortBy).descending();
    }
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<Product> productPage = productRepository.findAll(pageable);
    ;
    List<Product> productList = productPage.getContent();
    //		Long count = productList.stream().count();
    //		System.out.println(count);
    //		return productRepository.findAll();
    return productList;
  }

  public List<Product> findByKeyword(String keyword) {
    System.out.println("in findbykeyword method");
    return productRepository.findByNameContaining(keyword);
  }
}
