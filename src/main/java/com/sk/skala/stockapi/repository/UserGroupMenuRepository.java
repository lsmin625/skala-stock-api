package com.sk.skala.stockapi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.UserGroupMenu;
import com.sk.skala.stockapi.data.table.UserGroupMenuId;

@Repository
public interface UserGroupMenuRepository extends JpaRepository<UserGroupMenu, UserGroupMenuId> {
	List<UserGroupMenu> findAllByUserGroupId(String userGroupId);

	List<UserGroupMenu> findAllByUserGroupIdAndSystemId(String userGroupId, String systemId);

	List<UserGroupMenu> findAllByUserGroupIdAndSystemIdAndAuthLevelLessThan(String userGroupId, String systemId,
			int authLevel);

	Page<UserGroupMenu> findAllByUserGroupId(String userGroupId, Pageable pageable);
}