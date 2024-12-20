package com.sk.skala.stockapi.data.table;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserGroupMenuId implements Serializable {
	private static final long serialVersionUID = -3199995629329625754L;

	private String userGroupId;
	private String systemId;
	private String menuId;

	public UserGroupMenuId() {

	}

	public UserGroupMenuId(String userGroupId, String systemId, String menuId) {
		this.userGroupId = userGroupId;
		this.systemId = systemId;
		this.menuId = menuId;
	}

}
