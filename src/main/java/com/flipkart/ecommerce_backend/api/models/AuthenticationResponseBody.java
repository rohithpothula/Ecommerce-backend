package com.flipkart.ecommerce_backend.api.models;

public class AuthenticationResponseBody {
	
	private String jwtToken;

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

}
