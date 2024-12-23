package com.sk.skala.stockapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.repository.StockRepository;

@Configuration
public class StocksInitializer {

	@Bean
	CommandLineRunner initStocks(StockRepository repository) {
		return args -> {
			repository.save(new Stock("TechCorp", 100.00));
			repository.save(new Stock("GreenEnergy", 80.00));
			repository.save(new Stock("HealthPlus", 120.00));
		};
	}
}
