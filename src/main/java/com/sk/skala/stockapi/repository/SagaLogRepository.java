package com.sk.skala.stockapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.SagaHistory;

@Repository
public interface SagaLogRepository extends JpaRepository<SagaHistory, Long> {
	Page<SagaHistory> findBySagaId(String sagaId, Pageable pageable);
}