package com.example.moviebooking.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.moviebooking.model.Show;

public interface ShowRepository extends MongoRepository<Show, String> {
    List<Show> findByMovieId(String movieId);
}
