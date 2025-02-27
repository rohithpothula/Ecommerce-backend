package com.flipkart.ecommerce_backend.models.Repository;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.WebOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebOrderRepository extends JpaRepository<WebOrder, Long> {

  Page<WebOrder> findByLocalUser(LocalUser localUser, Pageable pageable);
}
