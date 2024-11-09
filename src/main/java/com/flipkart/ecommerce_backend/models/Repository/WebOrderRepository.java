package com.flipkart.ecommerce_backend.models.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.WebOrder;

public interface WebOrderRepository extends JpaRepository<WebOrder, Long>{
	
	Page<WebOrder> findByLocalUser(LocalUser localUser,Pageable pageable);

}
