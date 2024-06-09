package com.libray.MovieAi.services;

import com.libray.MovieAi.models.Rating;
import com.libray.MovieAi.models.RatingDto;

import java.util.List;

public interface RatingService {
    Rating addRating(RatingDto ratingDto);
    List<Rating> getRatingsByMovieId(int movieId);
}