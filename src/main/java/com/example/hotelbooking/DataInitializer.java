package com.example.hotelbooking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.hotelbooking.model.User;
import com.example.hotelbooking.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) { // Removed roomRepository parameter
        return args -> {
            // KEEP THIS: This ensures you can always log in as 'admin'
            if (userRepository.findByUsername("admin") == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123"); 
                userRepository.save(admin);
                System.out.println("Admin user created: admin / admin123");
            }
            
            // The logic for dummy rooms is gone because the API will provide real ones now.
        };
    }
}