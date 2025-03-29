package com.flipkart.ecommerce_backend.api.controllers.order;

import com.flipkart.ecommerce_backend.Constants.AppConstants;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.WebOrder;
import com.flipkart.ecommerce_backend.services.impl.OrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/orders")
public class OrderController {

  @Autowired private OrderService orderService;

  @GetMapping
  public List<WebOrder> getOrders(
      @AuthenticationPrincipal LocalUser authenticationPrinciple,
      @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)
          int pageNumber,
      @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false)
          int pageSize,
      @RequestParam(value = "sort", defaultValue = "id", required = false) String sortBy,
      @RequestParam(value = "sort_dir", defaultValue = AppConstants.SORT_DIR, required = false)
          String sortDir) {
    return orderService.getOrders(authenticationPrinciple, pageNumber, pageSize, sortBy);
  }
}
