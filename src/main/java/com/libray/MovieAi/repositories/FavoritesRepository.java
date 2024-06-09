package com.libray.MovieAi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.libray.MovieAi.models.Favorite;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorite, Integer> {
    Optional<Favorite> findByUserAndMovie(User user, Movie movie);
    void deleteById(Integer id);
}