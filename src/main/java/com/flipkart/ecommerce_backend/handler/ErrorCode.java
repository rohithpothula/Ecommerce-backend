package com.flipkart.ecommerce_backend.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // User Domain (1000-1999)
    USER_NOT_FOUND("USR-1000", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USR-1001", "User already exists", HttpStatus.CONFLICT),
    USER_NOT_VERIFIED("USR-1002", "User not verified", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("USR-1003", "Invalid user credentials", HttpStatus.UNAUTHORIZED),

    // Email Domain (2000-2999)
    EMAIL_SEND_FAILED("EML-2000", "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_NOT_FOUND("EML-2001", "Email not found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("EML-2002", "Email already exists", HttpStatus.CONFLICT),

    // Token Domain (3000-3999)
    TOKEN_EXPIRED("TKN-3000", "Verification token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("TKN-3001", "Invalid email verification token", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND("TKN-3002", "Verification token not found", HttpStatus.NOT_FOUND),
    TOKEN_NOT_SUPPORTED("TKN-3003", "Token not supported", HttpStatus.BAD_REQUEST), // Changed code to TKN-3003
    TOKEN_ALREADY_VERIFIED("TKN-3004", "Verification token already verified", HttpStatus.CONFLICT), // Changed code to TKN-3004

    // Validation Domain (4000-4999)
    VALIDATION_ERROR("VAL-4000", "Validation error", HttpStatus.BAD_REQUEST),

    // General/System Domain (5000-5999)
    INTERNAL_SERVER_ERROR("SYS-5000", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("SYS-5001", "Bad request", HttpStatus.BAD_REQUEST),

    // Database Domain (6000-6999)
    DATABASE_ERROR("DB-6000", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONSTRAINT_VIOLATION("DB-6001", "Database constraint violation", HttpStatus.BAD_REQUEST),
    DATABASE_CONCURRENCY_ERROR("DB-6002", "Concurrent modification detected", HttpStatus.CONFLICT),
    DATABASE_CONNECTION_ERROR("DB-6003", "Database connection failed", HttpStatus.SERVICE_UNAVAILABLE),

    UNAUTHORIZED_ACCESS("SEC-7000", "Unauthorized access", HttpStatus.FORBIDDEN),
    INSUFFICIENT_PERMISSIONS("SEC-7001", "Insufficient permissions", HttpStatus.FORBIDDEN),

    // Product Domain (8000-8999)
    PRODUCT_NOT_FOUND("PRD-8000", "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_EXISTS("PRD-8001", "Product already exists", HttpStatus.CONFLICT),
    INVALID_PRODUCT_STATUS("PRD-8002", "Invalid product status", HttpStatus.BAD_REQUEST),
    PRODUCT_VALIDATION_ERROR("PRD-8003", "Product validation error", HttpStatus.BAD_REQUEST),

    // Address Domain (9000-9999)
    ADDRESS_NOT_FOUND("ADR-9000", "Address not found", HttpStatus.NOT_FOUND),
    ADDRESS_ALREADY_EXISTS("ADR-9001", "Address already exists", HttpStatus.CONFLICT),
    INVALID_ADDRESS("ADR-9002", "Invalid address", HttpStatus.BAD_REQUEST),
    ADDRESS_VALIDATION_ERROR("ADR-9003", "Address validation error", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_VERIFIED("ADR-9004", "Address not verified", HttpStatus.FORBIDDEN),
    ADDRESS_VERIFICATION_FAILED("ADR-9005", "Address verification failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Role Domain (10000-10999)
    ROLE_NOT_FOUND("ROL-10000", "Role not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS("ROL-10001", "Role already exists", HttpStatus.CONFLICT),
    ROLE_VALIDATION_ERROR("ROL-10002", "Role validation error", HttpStatus.BAD_REQUEST),
    ROLE_ASSIGNMENT_FAILED("ROL-10003", "Role assignment failed", HttpStatus.INTERNAL_SERVER_ERROR),

    //Authentication Domain (11000-11999)
    AUTHENTICATION_FAILED("AUTH-11000", "Authentication failed", HttpStatus.UNAUTHORIZED),
    // Authorization Failed
    ACCESS_DENIED("AUTH-11001", "Access denied", HttpStatus.FORBIDDEN),
    // Product SKU Domain (12000-12999)
    SKU_ALREADY_EXISTS("SKU-12000", "SKU already exists", HttpStatus.CONFLICT);
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    public String getFormattedMessage(Object... args) {
        return String.format(defaultMessage, args);
    }

    public static ErrorCode fromCode(String code) {
        return Arrays.stream(ErrorCode.values())
                .filter(errorCode -> errorCode.getCode().equals(code))
                .findFirst()
                .orElse(INTERNAL_SERVER_ERROR);
    }

    public boolean isClientError() {
        return httpStatus.is4xxClientError();
    }

    public boolean isServerError() {
        return httpStatus.is5xxServerError();
    }
}
