package com.flipkart.ecommerce_backend.models.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.WebOrder;

public interface WebOrderRepository extends ListCrudRepository<WebOrder, Long>{
	
	List<WebOrder> findByLocalUser(LocalUser localUser);

}
