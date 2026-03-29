package com.example.hotelbooking.model;

import jakarta.persistence.*;

@Entity
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String role;       // e.g. Receptionist, Housekeeping, Manager
    private String phone;
    private String email;

    // Which hotel this staff member is assigned to
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}