package com.sk.skala.stockapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;

public interface PlayerStockRepository extends JpaRepository<PlayerStock, Long> {
	Optional<PlayerStock> findByPlayerAndStockId(Player player, Long stockId);

}
