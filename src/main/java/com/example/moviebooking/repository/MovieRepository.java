package com.example.moviebooking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.moviebooking.model.Movie;

public interface MovieRepository extends MongoRepository<Movie, String> {
}
