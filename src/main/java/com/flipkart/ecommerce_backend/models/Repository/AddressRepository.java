package com.flipkart.ecommerce_backend.models.Repository;

import com.flipkart.ecommerce_backend.models.Address;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface AddressRepository extends ListCrudRepository<Address, Long> {

  List<Address> findByLocalUser_Id(Long id);
}
