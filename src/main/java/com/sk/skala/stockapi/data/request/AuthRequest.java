package com.sk.skala.stockapi.data.request;

import lombok.Data;

@Data
public class AuthRequest {
	private String customerId;
	private String customerPhone;
}
