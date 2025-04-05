package com.flipkart.ecommerce_backend.repository;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.WebOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebOrderRepository extends JpaRepository<WebOrder, Long> {

  Page<WebOrder> findByLocalUser(LocalUser localUser, Pageable pageable);
}
