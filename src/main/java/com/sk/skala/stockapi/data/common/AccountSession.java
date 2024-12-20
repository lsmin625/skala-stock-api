package com.sk.skala.stockapi.data.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sk.skala.stockapi.data.table.UserGroupApi.AuthApi;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountSession {
	private String userId;
	private String userGroupId;
	private String userName;
	private String userEmail;
	private String userPhone;
	private String sessionId;
	private List<AuthMenu> authMenus;
	private List<AuthApi> allowedApis;
	private List<AuthApi> deniedApis;

	@Data
	public static class AuthMenu {
		private String systemId;
		private String systemName;
		private String menuId;
		private String menuName;
		private int authLevel;
	}
}
