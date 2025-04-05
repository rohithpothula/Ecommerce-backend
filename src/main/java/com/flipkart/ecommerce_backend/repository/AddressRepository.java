package com.flipkart.ecommerce_backend.repository;

import com.flipkart.ecommerce_backend.models.Address;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends ListCrudRepository<Address, UUID> {

  Page<Address> findByLocalUser_Id(UUID id, Pageable pageable);
  Optional<Address> findByIdAndLocalUser_Id(UUID addressId, UUID userId);

}
