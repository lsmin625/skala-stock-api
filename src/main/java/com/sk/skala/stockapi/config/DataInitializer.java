package com.sk.skala.stockapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.StockRepository;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner initStocks(StockRepository stockRepository, PlayerRepository playerRepository) {
		return args -> {
			stockRepository.save(new Stock("TechCorp", 100.00));
			stockRepository.save(new Stock("GreenEnergy", 80.00));
			stockRepository.save(new Stock("HealthPlus", 120.00));
			stockRepository.save(new Stock("SkalaEdu", 150.00));

			playerRepository.save(new Player("SkalaMan", 10000));
			playerRepository.save(new Player("SmartWoman", 10000));
		};
	}
}
