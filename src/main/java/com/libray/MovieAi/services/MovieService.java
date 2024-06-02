package com.libray.MovieAi.services;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.libray.MovieAi.models.Movie;

@Service
public class MovieService {

    @Autowired
    private MoviesRepository repo;

    public Optional<Movie> getMovieById(Integer id) {
        return repo.findById(id);
    }

}
