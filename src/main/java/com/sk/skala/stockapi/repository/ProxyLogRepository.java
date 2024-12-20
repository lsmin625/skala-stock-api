package com.sk.skala.stockapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.ProxyLog;

@Repository
public interface ProxyLogRepository extends JpaRepository<ProxyLog, Long> {
	Page<ProxyLog> findByApiPathLike(String keyword, Pageable pageable);

	Page<ProxyLog> findByRequestParamsLikeOrRequestBodyLike(String keyword1, String keyword2, Pageable pageable);

	Page<ProxyLog> findByApiResultAndApiPathLike(String keyword1, String keyword2, Pageable pageable);

	Page<ProxyLog> findByResponseBodyLike(String keyword, Pageable pageable);

	Page<ProxyLog> findByElapsedTimeGreaterThan(long time, Pageable pageable);

	Page<ProxyLog> findByElapsedTimeGreaterThanAndElapsedTimeLessThan(long min, long max, Pageable pageable);
}