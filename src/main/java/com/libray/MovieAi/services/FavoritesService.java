package com.libray.MovieAi.services;



import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Transactional
    public void addFavorite(User user, Movie movie) {
        Favorite favorite = new Favorite(user, movie);
        favoritesRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(User user, Movie movie) {
        favoritesRepository.deleteByUserAndMovie(user, movie);
        System.out.println("Favorite removed successfully.");
    }


    public boolean isFavorite(User user, Movie movie) {
        return favoritesRepository.findByUserAndMovie(user, movie).isPresent();
    }
}