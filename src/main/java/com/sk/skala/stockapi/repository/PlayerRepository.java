package com.sk.skala.stockapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.table.Player;

public interface PlayerRepository extends JpaRepository<Player, String> {
	Page<Player> findAll(Pageable pageable);
}
