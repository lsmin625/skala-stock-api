package com.sk.skala.stockapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.repository.StockRepository;

@Service
public class StockService {

	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	public List<Stock> getAllStocks() {
		return stockRepository.findAll();
	}

	public Optional<Stock> getStockById(Long id) {
		return stockRepository.findById(id);
	}

	public Stock createStock(Stock stock) {
		return stockRepository.save(stock);
	}

	public Stock updateStock(Long id, Stock stockDetails) {
		return stockRepository.findById(id).map(stock -> {
			stock.setName(stockDetails.getName());
			stock.setPrice(stockDetails.getPrice());
			return stockRepository.save(stock);
		}).orElseThrow(() -> new RuntimeException("Unknown Stock ID=" + id));
	}

	public void deleteStock(Long id) {
		stockRepository.deleteById(id);
	}
}
