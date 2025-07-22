package com.sk.skala.stockapi.data.table;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sk.skala.stockapi.tools.JsonTool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Player {

	@Id
	private String playerId;

	private String playerPassword;

	private double playerMoney;

	@JsonIgnore
	@Column(columnDefinition = "TEXT")
	private String playerStocks;

	public Player() {
	}

	public Player(String id, double money) {
		this.playerId = id;
		this.playerMoney = money;
	}

	public List<PlayerStock> getPlayerStockList() {
		if (this.playerStocks != null) {
			return JsonTool.toList(playerStocks, PlayerStock.class);
		} else {
			return new ArrayList<PlayerStock>();
		}
	}

	public void setPlayerStockList(List<PlayerStock> list) {
		this.playerStocks = JsonTool.toString(list);
	}
}
