package com.example.hotelbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.User;
@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
	List<Booking> findByUser(User user);
	}
