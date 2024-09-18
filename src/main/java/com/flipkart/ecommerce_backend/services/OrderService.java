package com.flipkart.ecommerce_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.WebOrder;
import com.flipkart.ecommerce_backend.models.Repository.WebOrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private WebOrderRepository webOrderRepository;
	
	public List<WebOrder> getOrders(LocalUser localUser){
		return webOrderRepository.findByLocalUser(localUser);
	}

}
