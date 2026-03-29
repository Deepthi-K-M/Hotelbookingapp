package com.example.hotelbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.model.Room;
import com.example.hotelbooking.model.Staff;
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
List<Staff> findByRoom(Room room);
List<Staff>findByRoomId(String roomId);
 }
