package com.flipkart.ecommerce_backend.exception.address;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Base exception for address-related business errors
 */
public class AddressException extends BusinessException {
    protected AddressException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
