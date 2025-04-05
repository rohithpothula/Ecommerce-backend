package com.flipkart.ecommerce_backend.exception.role;

import com.flipkart.ecommerce_backend.handler.ErrorCode;

public class RoleNotFoundException extends RoleException{
    public RoleNotFoundException(String roleId) {
        super(ErrorCode.ROLE_NOT_FOUND, "Role with ID " + roleId + " not found");
    }
}
