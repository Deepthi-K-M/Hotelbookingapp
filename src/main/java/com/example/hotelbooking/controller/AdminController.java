package com.example.hotelbooking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.Room;
import com.example.hotelbooking.model.Staff;
import com.example.hotelbooking.repository.BookingRepository;
import com.example.hotelbooking.repository.RoomRepository;
import com.example.hotelbooking.repository.StaffRepository;

@Controller
public class AdminController {

    @Autowired private RoomRepository    roomRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private StaffRepository   staffRepository;

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        List<Room>    allRooms    = roomRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAll();
        List<Staff>   allStaff   = staffRepository.findAll();

        double totalRevenue = allRooms.stream()
            .mapToDouble(room -> room.getPrice() * room.getBookedCount())
            .sum();

        model.addAttribute("rooms",        allRooms);
        model.addAttribute("bookings",     allBookings);
        model.addAttribute("staffList",    allStaff);
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin";
    }

    @PostMapping("/admin/add-room")
    public String addRoom(@RequestParam String roomName,
                          @RequestParam double price,
                          @RequestParam int totalRooms) {
        Room room = new Room();
        room.setName(roomName);
        room.setTotalRooms(totalRooms);
        room.setBookedCount(0);
        room.setPrice(price);
        room.setBooked(false);
        roomRepository.save(room);
        return "redirect:/admin";
    }

    @PostMapping("/admin/cancel/{id}")
    public String cancelBooking(@PathVariable String id) {
        Room room = roomRepository.findById(id).orElseThrow();
        if (room.getBookedCount() > 0) {
            room.setBookedCount(room.getBookedCount() - 1);
            if (room.getBookedCount() == 0) room.setBooked(false);
            roomRepository.save(room);
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/add-staff")
    public String addStaff(@RequestParam String staffName,
                           @RequestParam String staffRole,
                           @RequestParam String staffPhone,
                           @RequestParam(required = false) String staffEmail,
                           @RequestParam String hotelId) {

        Room room = roomRepository.findById(hotelId).orElseThrow();

        Staff staff = new Staff();
        staff.setName(staffName);
        staff.setRole(staffRole);
        staff.setPhone(staffPhone);
        staff.setEmail(staffEmail);
        staff.setRoom(room);
        staffRepository.save(staff);

        return "redirect:/admin#staff";
    }

    @PostMapping("/admin/delete-staff/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffRepository.deleteById(id);
        return "redirect:/admin#staff";
    }
}