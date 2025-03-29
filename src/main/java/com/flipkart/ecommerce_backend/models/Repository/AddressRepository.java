package com.flipkart.ecommerce_backend.models.Repository;

import com.flipkart.ecommerce_backend.models.Address;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

public interface AddressRepository extends ListCrudRepository<Address, Long> {

  List<Address> findByLocalUser_Id(UUID id);
  Optional<Address> findByIdAndLocalUser_Id(Long addressId, UUID userId);

}
