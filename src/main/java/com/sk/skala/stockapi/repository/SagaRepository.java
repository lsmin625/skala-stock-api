package com.sk.skala.stockapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.SagaTransaction;

@Repository
public interface SagaRepository extends JpaRepository<SagaTransaction, String> {
	Page<SagaTransaction> findBySagaNameLike(String name, Pageable pageable);
}