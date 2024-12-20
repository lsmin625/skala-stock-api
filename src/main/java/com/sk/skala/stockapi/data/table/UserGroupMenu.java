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
@Table(name = "bff_user_group_menu")
@IdClass(UserGroupMenuId.class)
public class UserGroupMenu extends Auditable<String> {

	@Id
	private String userGroupId;
	@Id
	private String systemId;
	@Id
	private String menuId;

	private String userGroupName;
	private String systemName;
	private String menuName;
	private int authLevel;
}