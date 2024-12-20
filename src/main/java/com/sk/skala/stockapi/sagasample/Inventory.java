package com.sk.skala.stockapi.sagasample;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sk.skala.stockapi.data.table.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "saga_inventory")
public class Inventory extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long inventoryId;

	private Long productId;
	private String productName;
	private Long orderId;
	private String orderDescription;

	@Enumerated(EnumType.STRING)
	private InventoryStatus inventoryStatus;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date reservedDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date releasedDate;

	public enum InventoryStatus {
		RESERVED, ALLOCATED, RELEASED
	}
}
