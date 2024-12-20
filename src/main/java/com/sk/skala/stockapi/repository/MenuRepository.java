package com.sk.skala.stockapi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.Menu;
import com.sk.skala.stockapi.data.table.MenuMstId;

@Repository
public interface MenuRepository extends JpaRepository<Menu, MenuMstId> {
	Page<Menu> findByMenuNameLike(String menuName, Pageable pageable);

	Page<Menu> findBySystemIdAndMenuNameLike(String systemId, String menuName, Pageable pageable);

	@Query("SELECT m.systemId, m.systemName FROM Menu m GROUP BY m.systemId")
	List<Menu> findAllGroupedBySystemId();

	List<Menu> findAllBySystemId(String systemId);
}