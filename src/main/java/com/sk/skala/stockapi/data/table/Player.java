package com.sk.skala.stockapi.data.table;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Player {

	@Id
	private String playerId;

	private double playerMoney;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "player_id")
	private List<PlayerStock> playerStocks = new ArrayList<>();

}
