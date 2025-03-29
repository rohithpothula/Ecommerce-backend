package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

/**
 * Thrown when an email cannot be sent due to system or configuration issues
 */
public class EmailSendException extends EmailException {
    public EmailSendException(String email, String reason) {
        super(ErrorCode.EMAIL_SEND_FAILED,
                String.format(ErrorCode.EMAIL_SEND_FAILED.getDefaultMessage(), email, reason));
    }}
