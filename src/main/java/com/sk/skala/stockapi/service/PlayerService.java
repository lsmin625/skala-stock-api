package com.sk.skala.stockapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.PlayerStockRepository;
import com.sk.skala.stockapi.repository.StockRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

	private final StockRepository stockRepository;
	private final PlayerRepository playerRepository;
	private final PlayerStockRepository playerStockRepository;

	public Response getAllPlayers(int offset, int count) {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Order.asc("playerId")));
		Page<Player> paged = playerRepository.findAll(pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(offset);
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response getPlayerById(String playerId) {
		Optional<Player> option = playerRepository.findById(playerId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		Response response = new Response();
		response.setBody(option.get());
		return response;
	}

	public Response createPlayer(Player player) {
		Optional<Player> option = playerRepository.findById(player.getPlayerId());
		if (!option.isEmpty()) {
			throw new ResponseException(Error.DATA_DUPLICATED);
		}
		playerRepository.save(player);

		return new Response();
	}

	public Response updatePlayer(Player player) {
		Optional<Player> option = playerRepository.findById(player.getPlayerId());
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		playerRepository.save(player);
		return new Response();
	}

	public Response deletePlayer(Player player) {
		Optional<Player> option = playerRepository.findById(player.getPlayerId());
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		playerRepository.deleteById(player.getPlayerId());
		return new Response();
	}

	@Transactional
	public Response buyPlayerStock(String playerId, Long stockId, int quantity) {
		Optional<Player> optionalPlayer = playerRepository.findById(playerId);
		if (optionalPlayer.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Player player = optionalPlayer.get();

		Optional<Stock> optionalStock = stockRepository.findById(stockId);
		if (optionalStock.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Stock stock = optionalStock.get();

		double totalCost = stock.getStockPrice() * quantity;

		if (totalCost > player.getPlayerMoney()) {
			throw new ResponseException(Error.INSUFFICIENT_FUNDS);
		}

		player.setPlayerMoney(player.getPlayerMoney() - totalCost);

		PlayerStock playerStock = new PlayerStock();
		playerStock.setPlayer(player);
		playerStock.setStock(stock);
		playerStock.setStockQuantity(quantity);

		PlayerStock savedStock = playerStockRepository.save(playerStock);

		player.getPlayerStocks().add(savedStock);
		playerRepository.save(player);

		return new Response();
	}

	@Transactional
	public Response sellPlayerStock(String playerId, Long stockId, int quantity) {
		Optional<Player> optionalPlayer = playerRepository.findById(playerId);
		if (optionalPlayer.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Player player = optionalPlayer.get();

		Optional<PlayerStock> optionalPlayerStock = playerStockRepository.findByPlayerAndStockId(player, stockId);
		if (optionalPlayerStock.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		PlayerStock playerStock = optionalPlayerStock.get();
		Stock stock = playerStock.getStock();

		if (quantity > playerStock.getStockQuantity()) {
			throw new ResponseException(Error.INSUFFICIENT_QUANTITY);
		}

		double totalEarnings = stock.getStockPrice() * quantity;

		player.setPlayerMoney(player.getPlayerMoney() + totalEarnings);

		playerStock.setStockQuantity(playerStock.getStockQuantity() - quantity);

		if (playerStock.getStockQuantity() == 0) {
			player.getPlayerStocks().remove(playerStock);
		}

		playerStockRepository.save(playerStock);
		playerRepository.save(player);

		return new Response();
	}

}
