package com.flipkart.ecommerce_backend.exception.user;

import com.flipkart.ecommerce_backend.security.exception.TokenException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Thrown when attempting to use a verification token for an already verified account
 */
public class UserVerificationTokenAlreadyVerifiedException extends TokenException {
    public UserVerificationTokenAlreadyVerifiedException() {
        super(
            ErrorCode.TOKEN_ALREADY_VERIFIED,
            "The verification token has already been used or the account is active."
        );
    }

    public UserVerificationTokenAlreadyVerifiedException(String userId) {
        super(
            ErrorCode.TOKEN_ALREADY_VERIFIED,
            "No further verification is needed for this account."
        );
    }
}
