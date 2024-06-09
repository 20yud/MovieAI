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

    public void addFavorite(User user, Movie movie) {
        Favorite favorite = new Favorite(user, movie);
        favoritesRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(User user, Movie movie) {
        // Find the favorite based on user_id and movie_id
        Optional<Favorite> favorite = favoritesRepository.findByUserAndMovie(user, movie);
        favorite.ifPresent(f -> {
            Integer favoriteId = f.getId();
            System.out.println("Favorite ID to be removed: " + favoriteId); // Print the ID before deletion
            // Delete the favorite by its id
            favoritesRepository.deleteById(favoriteId);
            System.out.println("Favorite removed successfully.");
        });
    }

    public boolean isFavorite(User user, Movie movie) {
        return favoritesRepository.findByUserAndMovie(user, movie).isPresent();
    }
}