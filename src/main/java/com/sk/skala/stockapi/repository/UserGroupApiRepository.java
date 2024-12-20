package com.sk.skala.stockapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.UserGroupApi;

@Repository
public interface UserGroupApiRepository extends JpaRepository<UserGroupApi, Long> {
	Optional<UserGroupApi> findByUserGroupId(String userGroupId);

	Page<UserGroupApi> findAllByUserGroupId(String userGroupId, Pageable pageable);
}