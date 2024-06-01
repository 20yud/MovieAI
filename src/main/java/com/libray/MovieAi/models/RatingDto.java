package com.libray.MovieAi.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class RatingDto {

    private int id; // Add the id field

    private int userId; // ID of the user who made the rating

    private int movieId; // ID of the movie being rated

    @NotNull(message = "The rating is required")
    @Min(value = 0, message = "The rating must be at least 0")
    @Max(value = 10, message = "The rating must be at most 10")
    private Double rating;

    private LocalDateTime ratingDate; // Rating date

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDateTime getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDateTime ratingDate) {
        this.ratingDate = ratingDate;
    }
}
