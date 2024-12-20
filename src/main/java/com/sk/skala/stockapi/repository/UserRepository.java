package com.sk.skala.stockapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.stockapi.data.table.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Page<User> findByUserNameLike(String userName, Pageable pageable);
}