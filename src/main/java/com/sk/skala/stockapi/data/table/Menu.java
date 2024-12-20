package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "bff_menu_mst")
@IdClass(MenuMstId.class)
public class Menu extends Auditable<String> {

	@Id
	private String systemId;

	private String systemName;

	@Id
	private String menuId;

	private String menuName;
}