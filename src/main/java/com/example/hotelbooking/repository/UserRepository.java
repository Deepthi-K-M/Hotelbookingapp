package com.example.hotelbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotelbooking.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
