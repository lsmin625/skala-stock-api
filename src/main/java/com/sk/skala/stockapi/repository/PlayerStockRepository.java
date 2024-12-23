package com.sk.skala.stockapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;

public interface PlayerStockRepository extends JpaRepository<Player, String> {

	PlayerStock save(String playerId, PlayerStock stock);
}
