package com.example.hotelbooking.controller;

import com.example.hotelbooking.model.User;
import com.example.hotelbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired private UserRepository userRepository;

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/signup")
    public String signup() { return "signup"; }

    @PostMapping("/signup")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam(required = false) String email) {
        if (userRepository.findByUsername(username) != null) {
            return "redirect:/signup?error=exists";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        // Store email if provided so confirmation emails can be sent
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email.trim());
        }
        userRepository.save(user);
        return "redirect:/login?success";
    }
}