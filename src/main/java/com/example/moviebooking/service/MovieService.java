package com.example.moviebooking.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.moviebooking.dto.MovieRequest;
import com.example.moviebooking.model.Movie;
import com.example.moviebooking.repository.MovieRepository;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Cacheable(cacheNames = "movies:all")
    public List<Movie> listMovies() {
        return movieRepository.findAll();
    }

    @Cacheable(cacheNames = "movies", key = "#id", unless = "#result == null")
    public Movie getMovieById(String id) {
        return movieRepository.findById(id).orElse(null);
    }

    @CacheEvict(cacheNames = {"movies:all", "movies"}, allEntries = true)
    public Movie createMovie(MovieRequest request) {
        String title = request.getTitle().trim();
        String posterUrl = request.getPosterUrl().trim();
        Movie movie = new Movie(title, request.getDuration(), posterUrl);
        return movieRepository.save(movie);
    }

    public boolean movieExists(String id) {
        return getMovieById(id) != null;
    }
}
