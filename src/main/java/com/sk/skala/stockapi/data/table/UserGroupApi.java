package com.sk.skala.stockapi.data.table;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "bff_user_group_api")
public class UserGroupApi extends Auditable<String> {
	@Id
	private Long id;

	private String userGroupId;
	private String userGroupName;

	@JsonIgnore
	private String allowedApis;

	@JsonIgnore
	private String deniedApis;

	public List<AuthApi> getAllowedApiList() {
		if (this.allowedApis != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return objectMapper.readValue(this.allowedApis, new TypeReference<List<AuthApi>>() {
				});
			} catch (Exception e) {
				throw new RuntimeException("Failed to parse allowedApiList JSON object", e);
			}
		} else {
			return new ArrayList<AuthApi>();
		}
	}

	public void setAllowedApiList(List<AuthApi> allowedApiList) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.allowedApis = objectMapper.writeValueAsString(allowedApiList);
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize allowedApiList to JSON string", e);
		}
	}

	public List<AuthApi> getDeniedApiList() {
		if (this.deniedApis != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return objectMapper.readValue(this.deniedApis, new TypeReference<List<AuthApi>>() {
				});
			} catch (Exception e) {
				throw new RuntimeException("Failed to parse deniedApiList JSON object", e);
			}
		} else {
			return new ArrayList<AuthApi>();
		}
	}

	public void setDeniedApiList(List<AuthApi> deniedApiList) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.deniedApis = objectMapper.writeValueAsString(deniedApiList);
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize deniedApiList to JSON string", e);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	public static class AuthApi {
		private String apiPath;
		private String apiName;

	}
}
