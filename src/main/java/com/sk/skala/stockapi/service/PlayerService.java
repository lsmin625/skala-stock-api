package com.sk.skala.stockapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.PlayerStockRepository;

import jakarta.transaction.Transactional;

@Service
public class PlayerService {

	private final PlayerRepository playerRepository;
	private final PlayerStockRepository playerStockRepository;

	public PlayerService(PlayerRepository playerRepository, PlayerStockRepository playerStockRepository) {
		this.playerRepository = playerRepository;
		this.playerStockRepository = playerStockRepository;
	}

	public List<Player> getAllPlayers() {
		return playerRepository.findAll();
	}

	public Optional<Player> getPlayerById(String playerId) {
		return playerRepository.findById(playerId);
	}

	public Player createPlayer(Player player) {
		return playerRepository.save(player);
	}

	public Player updatePlayer(String playerId, Player playerDetails) {
		return playerRepository.findById(playerId).map(player -> {
			player.setPlayerMoney(playerDetails.getPlayerMoney());
			player.setPlayerStocks(playerDetails.getPlayerStocks());
			return playerRepository.save(player);
		}).orElseThrow(() -> new RuntimeException("Player not found with id " + playerId));
	}

	public void deletePlayer(String playerId) {
		playerRepository.deleteById(playerId);
	}

	@Transactional
	public PlayerStock addPlayerStock(String playerId, PlayerStock stock) {
		Player player = playerRepository.findById(playerId)
				.orElseThrow(() -> new RuntimeException("Player not found with id " + playerId));

		stock.setPlayer(player);

		PlayerStock savedStock = playerStockRepository.save(stock);
		player.getPlayerStocks().add(savedStock);

		playerRepository.save(player);

		return savedStock;
	}

}
