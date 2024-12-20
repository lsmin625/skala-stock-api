package com.sk.skala.stockapi.data.table;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.ToString;

@Data
@Embeddable
@ToString
public class MenuMstId implements Serializable {
	private static final long serialVersionUID = -3199995620329625754L;

	private String systemId;
	private String menuId;

	public MenuMstId() {

	}

	public MenuMstId(String systemId, String menuId) {
		this.systemId = systemId;
		this.menuId = menuId;
	}
}
