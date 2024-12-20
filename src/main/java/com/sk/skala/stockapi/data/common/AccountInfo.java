package com.sk.skala.stockapi.data.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInfo {
	private String userId;
	private String userPassword;
	private String userGroupId;
	private String userName;
	private String userEmail;
	private String userPhone;
}
