package com.libray.MovieAi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.libray.MovieAi.models.Rating;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByMovieId(int movieId);
}