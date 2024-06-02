package com.libray.MovieAi.services;



import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.libray.MovieAi.models.Favorite;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.repositories.FavoritesRepository;

@Service
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;

    public FavoritesService(FavoritesRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }

    public void addFavorite(User user, Movie movie) {
        Favorite favorite = new Favorite(user, movie);
        favoritesRepository.save(favorite);
    }

    public void removeFavorite(User user, Movie movie) {
        Optional<Favorite> favorite = favoritesRepository.findByUserAndMovie(user, movie);
        favorite.ifPresent(favoritesRepository::delete);
    }

    public boolean isFavorite(User user, Movie movie) {
        return favoritesRepository.findByUserAndMovie(user, movie).isPresent();
    }
}