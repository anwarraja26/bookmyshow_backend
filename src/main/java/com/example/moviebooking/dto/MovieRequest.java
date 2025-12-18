package com.example.moviebooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MovieRequest {
    @NotBlank(message = "Movie title is required")
    private String title;

    @Min(value = 1, message = "Movie duration must be at least 1 minute")
    private int duration;

    @NotBlank(message = "Poster URL is required")
    @Size(max = 512, message = "Poster URL is too long")
    @Pattern(regexp = "^(?i)(http|https)://.+$", message = "Poster URL must start with http or https")
    private String posterUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
