package com.flipkart.ecommerce_backend.exception.address;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.hander.ErrorCode;

public class AddressNotFoundException extends AddressException {
    public AddressNotFoundException(String addressId) {
        super(ErrorCode.ADDRESS_NOT_FOUND, String.format("Address with email '%s' not found.", addressId));
    }
}
