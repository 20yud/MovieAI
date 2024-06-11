package com.libray.MovieAi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.libray.MovieAi.models.Favorite;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;

import jakarta.transaction.Transactional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorite, Integer> {
    Optional<Favorite> findByUserAndMovie(User user, Movie movie);
   
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user = ?1 AND f.movie = ?2")
    void deleteByUserAndMovie(User user, Movie movie);
}