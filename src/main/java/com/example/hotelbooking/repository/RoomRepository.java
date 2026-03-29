package com.example.hotelbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.model.Room;
@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
	List<Room> findByNameContainingIgnoreCase(String name);
}
