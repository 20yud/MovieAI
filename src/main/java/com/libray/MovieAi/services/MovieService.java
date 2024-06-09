package com.libray.MovieAi.services;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.libray.MovieAi.models.Movie;

@Service
public class MovieService {

    @Autowired
    private MoviesRepository repo;

    public Optional<Movie> getMovieById(Integer id) {
        return repo.findById(id);
    }
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int getTotalMovies() {
        String sql = "SELECT COUNT(*) FROM Movies";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Map<String, Integer> getTotalMoviesByGenre() {
        String sql = "SELECT g.name, COUNT(mg.movie_id) AS count FROM Genres g " +
                     "JOIN movie_genre mg ON g.id = mg.genre_id GROUP BY g.name";
        return jdbcTemplate.query(sql, (rs, rowNum) -> 
            Map.entry(rs.getString("name"), rs.getInt("count"))
        ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM Users";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
