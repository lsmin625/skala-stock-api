package com.sk.skala.stockapi.data.request;

import lombok.Data;

@Data
public class MaskingRequest {
	private String userId;
	private String customerId;
	private String authCode;
}
