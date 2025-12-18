package com.example.moviebooking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.moviebooking.dto.MovieRequest;
import com.example.moviebooking.model.Movie;
import com.example.moviebooking.service.MovieService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> listMovies() {
        return movieService.listMovies();
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody MovieRequest request) {
        Movie saved = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
