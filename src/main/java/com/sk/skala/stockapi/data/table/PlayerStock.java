package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerStock extends Stock {

	private int stockQuantity;
}