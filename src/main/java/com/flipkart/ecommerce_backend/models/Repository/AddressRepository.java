package com.flipkart.ecommerce_backend.models.Repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import com.flipkart.ecommerce_backend.models.Address;

public interface AddressRepository extends ListCrudRepository<Address, Long>{
	
	List<Address> findByLocalUser_Id(Long id);

}
