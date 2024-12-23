package com.sk.skala.stockapi.config;

public enum Error {
	//@formatter:off
	
	SYSTEM_ERROR(9000, "SYSTEM_ERROR"),
	
	NOT_AUTHENTICATED(9001, "NOT_AUTHENTICATED"),
	NOT_AUTHORIZED(9002, "NOT_AUTHORIZED"),
	
	UNDEFINED_ERROR(9999, "UNDEFINED_ERROR");
	
	//@formatter:on

	private final int code;
	private final String message;

	Error(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}