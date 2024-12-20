package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "bff_api_mst")
public class ApiMst extends Auditable<String> {
	@Id
	private Long id;

	private String apiPath;
	private String apiName;
}
