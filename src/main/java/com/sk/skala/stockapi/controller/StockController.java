package com.sk.skala.stockapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.service.StockService;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

	private final StockService stockService;

	public StockController(StockService stockService) {
		this.stockService = stockService;
	}

	// Get all stocks
	@GetMapping
	public List<Stock> getAllStocks() {
		return stockService.getAllStocks();
	}

	// Get stock by ID
	@GetMapping("/{id}")
	public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
		return stockService.getStockById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Create new stock
	@PostMapping
	public Stock createStock(@RequestBody Stock stock) {
		return stockService.createStock(stock);
	}

	// Update stock by ID
	@PutMapping("/{id}")
	public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stockDetails) {
		try {
			return ResponseEntity.ok(stockService.updateStock(id, stockDetails));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Delete stock by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
		try {
			stockService.deleteStock(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
