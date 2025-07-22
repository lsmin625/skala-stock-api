package com.sk.skala.stockapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.PlayerSession;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.StockRepository;
import com.sk.skala.stockapi.tools.StringTool;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

	private final StockRepository stockRepository;
	private final PlayerRepository playerRepository;
	private final SessionHandler sessionHandler;

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

	public Response createPlayer(Player playerSession) {
		if (StringTool.isAnyEmpty(playerSession.getPlayerId())
				|| StringTool.isAnyEmpty(playerSession.getPlayerPassword())) {
			throw new ParameterException("playerId", "playerPassword");
		}

		Optional<Player> option = playerRepository.findById(playerSession.getPlayerId());
		if (!option.isEmpty()) {
			throw new ResponseException(Error.DATA_DUPLICATED);
		}
		Player player = new Player();
		player.setPlayerId(playerSession.getPlayerId());
		player.setPlayerPassword(playerSession.getPlayerPassword());
		player.setPlayerMoney(100000);

		playerRepository.save(player);

		return new Response();
	}

	public Response loginPlayer(PlayerSession playerSession) {
		if (StringTool.isAnyEmpty(playerSession.getPlayerId())
				|| StringTool.isAnyEmpty(playerSession.getPlayerPassword())) {
			throw new ParameterException("playerId", "playerPassword");
		}

		Optional<Player> option = playerRepository.findById(playerSession.getPlayerId());
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		Player player = option.get();
		if (player.getPlayerPassword().equals(playerSession.getPlayerPassword())) {
			sessionHandler.storeAccessToken(playerSession);
		} else {
			throw new ResponseException(Error.NOT_AUTHENTICATED);
		}

		Response response = new Response();
		response.setBody(player);
		return response;
	}

	public Response updatePlayer(Player player) {
		if (StringTool.isAnyEmpty(player.getPlayerId()) || player.getPlayerMoney() <= 0) {
			throw new ParameterException("playerId", "playerMoney");
		}

		Optional<Player> option = playerRepository.findById(player.getPlayerId());
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Player storedPlayer = option.get();
		storedPlayer.setPlayerMoney(player.getPlayerMoney());
		playerRepository.save(storedPlayer);

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
	public Response buyPlayerStock(StockOrder order) {
		String playerId = sessionHandler.getPlayerId();

		Optional<Player> option = playerRepository.findById(playerId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Player player = option.get();

		Optional<Stock> optionalStock = stockRepository.findById(order.getStockId());
		if (optionalStock.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Stock stock = optionalStock.get();

		double totalCost = stock.getStockPrice() * order.getStockQuantity();

		if (totalCost > player.getPlayerMoney()) {
			throw new ResponseException(Error.INSUFFICIENT_FUNDS);
		}

		player.setPlayerMoney(player.getPlayerMoney() - totalCost);

		PlayerStock playerStock = new PlayerStock(stock, order.getStockQuantity());

		boolean stockExists = false;
		List<PlayerStock> playerStocks = player.getPlayerStockList();
		for (PlayerStock existingStock : playerStocks) {
			if (existingStock.getStockName().equals(playerStock.getStockName())) {
				existingStock.setStockQuantity(existingStock.getStockQuantity() + order.getStockQuantity());
				stockExists = true;
				break;
			}
		}
		if (!stockExists) {
			playerStocks.add(playerStock);
		}
		player.setPlayerStockList(playerStocks);

		playerRepository.save(player);

		return new Response();
	}

	@Transactional
	public Response sellPlayerStock(StockOrder order) {
		String playerId = sessionHandler.getPlayerId();

		Optional<Player> option = playerRepository.findById(playerId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Player player = option.get();

		Optional<Stock> optionalStock = stockRepository.findById(order.getStockId());
		if (optionalStock.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Stock stock = optionalStock.get();

		PlayerStock playerStock = new PlayerStock(stock, order.getStockQuantity());

		boolean stockExists = false;
		List<PlayerStock> playerStocks = player.getPlayerStockList();
		for (PlayerStock existingStock : playerStocks) {
			if (existingStock.getStockName().equals(playerStock.getStockName())) {
				if (order.getStockQuantity() > existingStock.getStockQuantity()) {
					throw new ResponseException(Error.INSUFFICIENT_QUANTITY);
				}
				existingStock.setStockQuantity(existingStock.getStockQuantity() - order.getStockQuantity());
				if (existingStock.getStockQuantity() == 0) {
					playerStocks.remove(existingStock);
				}
				stockExists = true;
				break;
			}
		}
		if (!stockExists) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		player.setPlayerStockList(playerStocks);

		double totalEarnings = stock.getStockPrice() * order.getStockQuantity();
		player.setPlayerMoney(player.getPlayerMoney() + totalEarnings);

		playerRepository.save(player);

		return new Response();
	}

}
