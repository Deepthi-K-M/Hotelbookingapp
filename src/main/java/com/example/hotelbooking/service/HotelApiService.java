package com.example.hotelbooking.service;

import com.example.hotelbooking.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class HotelApiService {

    public List<Room> fetchLiveHotels() {
        // Updated URL specifically for the Api Dojo Booking API
        String url = "https://apidojo-booking-v1.p.rapidapi.com/properties/list?offset=0&arrival_date=2026-05-09&departure_date=2026-05-10&guest_qty=1&dest_ids=-2092174&room_qty=1&search_type=city";
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        // Ensure this matches your dashboard exactly
        headers.set("X-RapidAPI-Key", "e442d71e3cmsh331c821beff5f8ap18d802jsnd177610eed33");
        headers.set("X-RapidAPI-Host", "apidojo-booking-v1.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            // Api Dojo returns a list under the key "result"
            List<Map<String, Object>> apiHotels = (List<Map<String, Object>>) response.getBody().get("result");
            
            List<Room> rooms = new ArrayList<>();
            if (apiHotels != null) {
                for (Map<String, Object> hotelData : apiHotels) {
                    Room room = new Room();
                    room.setId(hotelData.get("hotel_id").toString());
                    room.setName(hotelData.getOrDefault("hotel_name", "Luxury Hotel").toString());
                    
                    // Handle price extraction
                    Object price = hotelData.get("min_total_price");
                    room.setPrice(price != null ? Double.parseDouble(price.toString()) : 150.0);
                    
                    room.setTotalRooms(10); 
                    room.setBookedCount(0);
                    rooms.add(room);
                }
            }
            System.out.println("DEBUG: Successfully fetched " + rooms.size() + " hotels from API.");
            return rooms;
        } catch (Exception e) {
            System.err.println("API Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}