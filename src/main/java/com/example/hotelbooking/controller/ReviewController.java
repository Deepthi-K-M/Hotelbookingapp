package com.example.hotelbooking.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hotelbooking.model.Review;
import com.example.hotelbooking.model.Room;
import com.example.hotelbooking.model.User;
import com.example.hotelbooking.repository.ReviewRepository;
import com.example.hotelbooking.repository.RoomRepository;
import com.example.hotelbooking.repository.UserRepository;

@Controller
public class ReviewController {
	@Autowired private ReviewRepository reviewRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private UserRepository userRepository;

    @PostMapping("/leave-review/{roomId}")
    public String leaveReview(@PathVariable String roomId, 
                              @RequestParam int rating, 
                              @RequestParam String comment, 
                              Principal principal, 
                              RedirectAttributes ra) {
        
        Room room = roomRepository.findById(roomId).orElseThrow();
        User user = userRepository.findByUsername(principal.getName());

        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setRoom(room);
        review.setUser(user);

        reviewRepository.save(review);
        
        ra.addFlashAttribute("success", "Thank you for your feedback on " + room.getName() + "!");
        return "redirect:/my-bookings";
    }
}

