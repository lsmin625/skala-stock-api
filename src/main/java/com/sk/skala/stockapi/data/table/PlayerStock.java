package com.sk.skala.stockapi.data.table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerStock extends Stock {
	private int stockQuantity;

	public PlayerStock() {
	}

	public PlayerStock(Stock stock, int quantity) {
		super(stock.getStockName(), stock.getStockPrice());
		this.stockQuantity = quantity;
	}
}
