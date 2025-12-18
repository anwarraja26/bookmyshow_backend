package com.example.moviebooking.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.moviebooking.model.Booking;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByUserUserId(String userId);
}
