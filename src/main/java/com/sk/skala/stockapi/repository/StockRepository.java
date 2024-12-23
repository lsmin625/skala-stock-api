package com.sk.skala.stockapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.table.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
	// 추가적으로 커스텀 쿼리 메서드를 정의할 수 있습니다.
}
