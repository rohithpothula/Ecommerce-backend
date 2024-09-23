package com.flipkart.ecommerce_backend.api.models;

public class VerificationTokenResponseBody {
	
	private boolean success;
	
	private String failureReason;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

}
