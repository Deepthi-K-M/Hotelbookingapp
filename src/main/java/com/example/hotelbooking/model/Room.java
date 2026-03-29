package com.example.hotelbooking.model;

import java.util.List;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Room {

    @Id
    private String id;
    private String name;
    private boolean isBooked;
    private double price;
    private int totalRooms;
    private int bookedCount;
    private String imageUrl;
    private int capacityPerRoom = 2; // default 2 guests per room
    public Room() {}

    // FIX: Auto-generate UUID so admin-added rooms never get a null ID
    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        double sum = 0;
        for (Review r : reviews) sum += r.getRating();
        double avg = sum / reviews.size();
        return Math.round(avg * 10.0) / 10.0;
    }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean isBooked) { this.isBooked = isBooked; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public int getBookedCount() { return bookedCount; }
    public void setBookedCount(int bookedCount) { this.bookedCount = bookedCount; }
    public int getAvailableRooms() { return Math.max(0, this.totalRooms - this.bookedCount); }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

	public int getCapacityPerRoom() {
		return capacityPerRoom;
	}

	public void setCapacityPerRoom(int capacityPerRoom) {
		this.capacityPerRoom = capacityPerRoom;
	}
}