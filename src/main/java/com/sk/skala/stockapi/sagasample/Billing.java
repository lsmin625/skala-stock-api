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
@Table(name = "saga_billing")
public class Billing extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long billingId;

	private Long orderId;
	private String orderDescription;
	private Long customerId;
	private String customerName;

	@Enumerated(EnumType.STRING)
	private BillingStatus billingStatus;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date createdDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date updatedDate;

	public enum BillingStatus {
		PENDING, COMPLETED, REFUNDED
	}
}
