package com.libray.MovieAi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.Rating;
import com.libray.MovieAi.models.RatingDto;
import com.libray.MovieAi.repositories.RatingRepository;


import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MoviesRepository movieRepository;

    @Override
    @Transactional
    public Rating addRating(RatingDto ratingDto) {
        Rating rating = new Rating();
        rating.setUser(ratingDto.getUser());

        // Fetch the Movie entity from the repository
        Movie movie = movieRepository.findById(ratingDto.getMovieId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid movie ID"));

        rating.setMovie(movie);
        rating.setRating(ratingDto.getRating());
        rating.setRatingDate(ratingDto.getRatingDate() != null ? ratingDto.getRatingDate() : LocalDateTime.now());
        return ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getRatingsByMovieId(int movieId) {
        return ratingRepository.findByMovieId(movieId);
    }
}