package com.flipkart.ecommerce_backend.api.controllers.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.WebOrder;
import com.flipkart.ecommerce_backend.services.OrderService;

@RestController
@RequestMapping("api/orders")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping
	public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser authenticationPrinciple){
		return orderService.getOrders(authenticationPrinciple); 
	}

}
