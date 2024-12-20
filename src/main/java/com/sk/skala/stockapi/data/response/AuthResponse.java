package com.sk.skala.stockapi.data.response;

import com.sk.skala.stockapi.data.request.AuthRequest;

import lombok.Data;

@Data
public class AuthResponse {
	private String customerId;
	private String customerPhone;
	private String authCode;

	public AuthResponse() {
	}

	public AuthResponse(AuthRequest request) {
		this.customerId = request.getCustomerId();
		this.customerPhone = request.getCustomerPhone();
	}
}
