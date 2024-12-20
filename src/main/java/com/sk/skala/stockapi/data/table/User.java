package com.sk.skala.stockapi.data.table;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "bff_user_mst")
public class User extends Auditable<String> {
	@Id
	private String userId;
	private String userGroupId;
	private String userName;
	private String userPassword;
	private String userEmail;
	private String userPhone;
	private Date dtUpdatedPassword;
}
