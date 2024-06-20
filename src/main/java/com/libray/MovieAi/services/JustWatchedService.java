package com.libray.MovieAi.services;

import java.util.List;

import com.libray.MovieAi.models.JustWatched;
import com.libray.MovieAi.models.User;

public interface JustWatchedService {
    void addJustWatched(int userId, int movieId);
    List<JustWatched> getRecentWatchedMovies(int userId);
}