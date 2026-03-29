package com.example.hotelbooking.service;

import com.example.hotelbooking.model.User;
import com.example.hotelbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Logic: if username is 'admin', give ROLE_ADMIN, else ROLE_USER
        String role = "admin".equalsIgnoreCase(username) ? "ROLE_ADMIN" : "ROLE_USER";

        // Password handling: Ensure it has the {noop} prefix for plain text comparison
        String password = user.getPassword();
        if (!password.startsWith("{noop}")) {
            password = "{noop}" + password;
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(password)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }
}