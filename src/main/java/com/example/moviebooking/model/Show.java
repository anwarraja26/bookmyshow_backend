package com.example.moviebooking.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shows")
public class Show {
    @Id
    private String id;
    private String movieId;
    private String showTime;
    private int capacity;
    private int availableSeats;
    private List<String> seatLayout = new ArrayList<>();
    private Set<String> reservedSeats = new HashSet<>();

    public Show() {
    }

    public Show(String movieId, String showTime, int capacity) {
        this.movieId = movieId;
        this.showTime = showTime;
        this.capacity = capacity;
        this.availableSeats = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<String> getSeatLayout() {
        return seatLayout;
    }

    public void setSeatLayout(List<String> seatLayout) {
        this.seatLayout = seatLayout;
    }

    public Set<String> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(Set<String> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }
}
