package com.example.hotelbooking.model;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
@Entity
public class Booking {
	@Id 
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id") // Best practice to name the foreign key column
    private User user;

    @ManyToOne
    @JoinColumn(name="room_id",referencedColumnName="id")
    private Room room; // Links to the Room entity

    private LocalDateTime bookingDate;
    private String checkInDate;
    private String checkOutDate;
    private double totalCost;
    private int numberOfGuests;
    private int roomsAllocated;
    private String email;

    @PrePersist
    protected void onCreate() {
        this.bookingDate = LocalDateTime.now();
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(String checkInDate) {
		this.checkInDate = checkInDate;
	}

	public String getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public int getNumberOfGuests() {
		return numberOfGuests;
	}

	public void setNumberOfGuests(int numberOfGuests) {
		this.numberOfGuests = numberOfGuests;
	}

	public int getRoomsAllocated() {
		return roomsAllocated;
	}

	public void setRoomsAllocated(int roomsAllocated) {
		this.roomsAllocated = roomsAllocated;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
}
