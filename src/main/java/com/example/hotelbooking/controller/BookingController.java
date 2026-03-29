package com.example.hotelbooking.controller;

import com.example.hotelbooking.model.*;
import com.example.hotelbooking.repository.*;
import com.example.hotelbooking.service.EmailService;
import com.example.hotelbooking.service.PdfReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BookingController {

    @Autowired private BookingRepository  bookingRepository;
    @Autowired private RoomRepository     roomRepository;
    @Autowired private UserRepository     userRepository;
    @Autowired private EmailService       emailService;
    @Autowired private PdfReceiptService  pdfReceiptService;

    @GetMapping("/")
    public String home() { return "redirect:/rooms"; }

    @PostMapping("/book/{id}")
    public String processBooking(@PathVariable String id,
                                 @RequestParam("checkInDate")     String checkInDate,
                                 @RequestParam("checkOutDate")    String checkOutDate,
                                 @RequestParam("roomName")        String roomName,
                                 @RequestParam("price")           Double price,
                                 @RequestParam("numberOfGuests")  int numberOfGuests,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        // Validate dates
        LocalDate checkIn  = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);

        if (!checkOut.isAfter(checkIn)) {
            redirectAttributes.addFlashAttribute("error", "Check-out date must be after check-in date.");
            return "redirect:/rooms/" + id;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        User user   = userRepository.findByUsername(principal.getName());

        // Ghost room logic: save API room locally if not yet in DB
        Room room = roomRepository.findById(id).orElseGet(() -> {
            Room newRoom = new Room();
            newRoom.setId(id);
            newRoom.setName(roomName);
            newRoom.setPrice(price);
            newRoom.setTotalRooms(10);
            newRoom.setBookedCount(0);
            return roomRepository.save(newRoom);
        });

        // Calculate rooms needed based on guests (2 guests per room)
        int capacityPerRoom = room.getCapacityPerRoom() > 0 ? room.getCapacityPerRoom() : 2;
        int roomsNeeded     = (int) Math.ceil((double) numberOfGuests / capacityPerRoom);
        int availableRooms  = room.getTotalRooms() - room.getBookedCount();

        if (roomsNeeded > availableRooms) {
            redirectAttributes.addFlashAttribute("error",
                "Not enough rooms for " + numberOfGuests + " guest(s). Only " + availableRooms + " room(s) available.");
            return "redirect:/rooms/" + id;
        }

        double totalCost = nights * room.getPrice() * roomsNeeded;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setTotalCost(totalCost);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setRoomsAllocated(roomsNeeded);
        booking.setEmail(user.getEmail() != null ? user.getEmail() : "");
        bookingRepository.save(booking);

        room.setBookedCount(room.getBookedCount() + roomsNeeded);
        roomRepository.save(room);

        // Send confirmation email if user provided an email on signup
        String userEmail = user.getEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            emailService.sendBookingConfirmation(booking, userEmail);
        }

        redirectAttributes.addFlashAttribute("success",
            "Booking confirmed for " + room.getName() +
            "! " + roomsNeeded + " room(s) for " + numberOfGuests +
            " guest(s). Total: $" + String.format("%.2f", totalCost));
        return "redirect:/my-bookings";
    }

    @GetMapping("/my-bookings")
    public String viewMyBookings(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName());
        List<Booking> allBookings = bookingRepository.findByUser(user);
        String today = LocalDate.now().toString();

        List<Booking> upcoming = allBookings.stream()
            .filter(b -> b.getCheckInDate() != null && b.getCheckInDate().compareTo(today) >= 0)
            .collect(Collectors.toList());

        List<Booking> past = allBookings.stream()
            .filter(b -> b.getCheckInDate() != null && b.getCheckInDate().compareTo(today) < 0)
            .collect(Collectors.toList());

        model.addAttribute("upcoming", upcoming);
        model.addAttribute("past", past);
        return "my-bookings";
    }

    @PostMapping("/cancel-booking/{id}")
    public String cancelBooking(@PathVariable String id, Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        Booking booking = bookingRepository.findById(id).orElseThrow();
        if (!booking.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/my-bookings";
        }

        Room room = booking.getRoom();
        int roomsToFree = booking.getRoomsAllocated() > 0 ? booking.getRoomsAllocated() : 1;
        if (room.getBookedCount() > 0) {
            room.setBookedCount(Math.max(0, room.getBookedCount() - roomsToFree));
            roomRepository.save(room);
        }
        bookingRepository.delete(booking);

        redirectAttributes.addFlashAttribute("success",
            "Reservation for " + room.getName() + " cancelled.");
        return "redirect:/my-bookings";
    }

    // PDF Receipt
    @GetMapping("/download-receipt/{id}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable String id,
                                                   Principal principal) {
        if (principal == null) return ResponseEntity.status(403).build();

        Booking booking = bookingRepository.findById(id).orElseThrow();
        if (!booking.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        try {
            byte[] pdf      = pdfReceiptService.generateReceipt(booking);
            String filename = "Receipt_" + booking.getId().substring(0, 8).toUpperCase() + ".pdf";

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {
            System.err.println("PDF generation failed: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}