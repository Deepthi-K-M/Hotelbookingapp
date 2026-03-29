package com.example.hotelbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF disabled for development ease
            .authorizeHttpRequests(auth -> auth
            	    // Add "/" and "/rooms" to the permitAll list
            	    .requestMatchers("/", "/rooms", "/login", "/signup", "/css/**", "/js/**").permitAll()
            	    .requestMatchers("/admin/**").hasRole("ADMIN")
            	    .requestMatchers("/book/**").authenticated() 
            	    .anyRequest().authenticated()
            	)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/rooms", true) 
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}