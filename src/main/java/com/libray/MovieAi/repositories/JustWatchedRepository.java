package com.libray.MovieAi.repositories;

import com.libray.MovieAi.models.JustWatched;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JustWatchedRepository extends JpaRepository<JustWatched, Integer> {
    JustWatched findByUserAndMovie(User user, Movie movie);
    
    List<JustWatched> findByUserOrderByWatchDateDesc(User user);
}