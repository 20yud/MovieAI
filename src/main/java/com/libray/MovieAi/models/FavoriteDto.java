package com.libray.MovieAi.models;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class FavoriteDto {

    private int id; // Add the id field

    @NotNull(message = "The user ID is required")
    private int userId; // ID of the user who favorited the movie

    @NotNull(message = "The movie ID is required")
    private int movieId; // ID of the movie being favorited

    private LocalDateTime favoriteDate; // Favorite date

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

    public LocalDateTime getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(LocalDateTime favoriteDate) {
        this.favoriteDate = favoriteDate;
    }
}
