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

import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.service.PlayerService;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

	private final PlayerService playerService;

	public PlayerController(PlayerService playerService) {
		this.playerService = playerService;
	}

	// Get all players
	@GetMapping
	public List<Player> getAllPlayers() {
		return playerService.getAllPlayers();
	}

	// Get player by ID
	@GetMapping("/{playerId}")
	public ResponseEntity<Player> getPlayerById(@PathVariable String playerId) {
		return playerService.getPlayerById(playerId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Create new player
	@PostMapping
	public Player createPlayer(@RequestBody Player player) {
		return playerService.createPlayer(player);
	}

	// Update player by ID
	@PutMapping("/{playerId}")
	public ResponseEntity<Player> updatePlayer(@PathVariable String playerId, @RequestBody Player playerDetails) {
		try {
			return ResponseEntity.ok(playerService.updatePlayer(playerId, playerDetails));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Delete player by ID
	@DeleteMapping("/{playerId}")
	public ResponseEntity<Void> deletePlayer(@PathVariable String playerId) {
		try {
			playerService.deletePlayer(playerId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Add stock to player
	@PostMapping("/{playerId}/stocks")
	public ResponseEntity<PlayerStock> addPlayerStock(@PathVariable String playerId, @RequestBody PlayerStock stock) {
		try {
			return ResponseEntity.ok(playerService.addPlayerStock(playerId, stock));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
