package com.example.hotelbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotelbooking.model.Review;
import com.example.hotelbooking.model.Room;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByRoom(Room room);
}
