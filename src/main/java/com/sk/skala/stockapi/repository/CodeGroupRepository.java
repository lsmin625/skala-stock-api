package com.sk.skala.stockapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.CodeGroup;

@Repository
public interface CodeGroupRepository extends JpaRepository<CodeGroup, String> {
	Page<CodeGroup> findByCodeGroupNameLike(String name, Pageable pageable);
}