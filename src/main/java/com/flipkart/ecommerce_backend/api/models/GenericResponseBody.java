package com.flipkart.ecommerce_backend.api.models;

public class GenericResponseBody {
	
	private boolean Success;
	
	private String failureReason;

	public boolean getIsSuccess() {
		return Success;
	}

	public void setIsSuccess(boolean b) {
		this.Success = b;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
}
