package com.example.hotelbooking.controller;

import com.example.hotelbooking.model.Room;
import com.example.hotelbooking.model.Staff;
import com.example.hotelbooking.repository.RoomRepository;
import com.example.hotelbooking.repository.StaffRepository;
import com.example.hotelbooking.service.HotelApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RoomController {

    @Autowired private HotelApiService hotelApiService;
    @Autowired private RoomRepository  roomRepository;
    @Autowired private StaffRepository staffRepository;

    @GetMapping("/rooms")
    public String viewHomePage(Model model, Principal principal,
                               @RequestParam(required = false) String keyword) {

        if (principal != null && "admin".equalsIgnoreCase(principal.getName())) {
            return "redirect:/admin";
        }

        List<Room> rooms = hotelApiService.fetchLiveHotels();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String search = keyword.trim().toLowerCase();
            List<Room> filtered = rooms.stream()
                .filter(r -> r.getName().toLowerCase().contains(search))
                .collect(Collectors.toList());
            if (!filtered.isEmpty()) rooms = filtered;
        }

        model.addAttribute("rooms",   rooms);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    @GetMapping("/rooms/{id}")
    public String roomDetail(@PathVariable String id, Model model) {
        Room room = hotelApiService.fetchLiveHotels().stream()
            .filter(r -> r.getId().equals(id))
            .findFirst()
            .orElseGet(() -> roomRepository.findById(id).orElse(null));

        if (room == null) return "redirect:/rooms";

        roomRepository.findById(id).ifPresent(dbRoom -> {
            room.setBookedCount(dbRoom.getBookedCount());
            room.setTotalRooms(dbRoom.getTotalRooms());
            room.setReviews(dbRoom.getReviews());
        });

        // FIX: API rooms not yet in DB have totalRooms=0, show as available
        if (room.getTotalRooms() == 0) {
            room.setTotalRooms(10);
        }

        List<Staff> staffList = staffRepository.findByRoomId(id);
        model.addAttribute("room", room);
        model.addAttribute("staffList", staffList);
        return "room-detail";
    }
}