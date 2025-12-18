package com.example.moviebooking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.moviebooking.dto.ShowRequest;
import com.example.moviebooking.model.Show;
import com.example.moviebooking.service.MovieService;
import com.example.moviebooking.service.ShowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shows")
@Validated
public class ShowController {
    private final ShowService showService;
    private final MovieService movieService;

    public ShowController(ShowService showService, MovieService movieService) {
        this.showService = showService;
        this.movieService = movieService;
    }

    @GetMapping
    public List<Show> listShows(@RequestParam(name = "movieId", required = false) String movieId) {
        if (movieId == null || movieId.isBlank()) {
            return showService.listShows();
        }
        return showService.listShowsForMovie(movieId);
    }

    @PostMapping
    public ResponseEntity<Show> createShow(@Valid @RequestBody ShowRequest request) {
        if (!movieService.movieExists(request.getMovieId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found for show");
        }
        Show saved = showService.createShow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
