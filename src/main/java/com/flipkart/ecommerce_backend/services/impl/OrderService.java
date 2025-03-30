package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.repository.WebOrderRepository;
import com.flipkart.ecommerce_backend.models.WebOrder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  @Autowired private WebOrderRepository webOrderRepository;

  public List<WebOrder> getOrders(
      LocalUser localUser, int pageNumber, int pageSize, String sortBy) {
    Pageable p = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
    Page<WebOrder> orderPage = webOrderRepository.findByLocalUser(localUser, p);
    List<WebOrder> orderList = orderPage.getContent();
    return orderList;
  }
}
