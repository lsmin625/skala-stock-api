package com.sk.skala.stockapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.ApiMst;

@Repository
public interface ApiRepository extends JpaRepository<ApiMst, Long> {
	Page<ApiMst> findAllByApiPathLikeOrApiNameLike(String apiPath, String apiName, Pageable pageable);

	Optional<ApiMst> findByApiPath(String apiPath);
}