package com.libray.MovieAi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.libray.MovieAi.models.JustWatched;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.repositories.JustWatchedRepository;
import com.libray.MovieAi.repositories.UserRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JustWatchedServiceImpl implements JustWatchedService {

    @Autowired
    private JustWatchedRepository justWatchedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MoviesRepository movieRepository;
    
    @Override
    @Transactional
    public void addJustWatched(int userId, int movieId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        Movie movie = movieRepository.findById(movieId)
                                      .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + movieId));

        // Check if the combination of user_id and movie_id already exists
        JustWatched existingRecord = justWatchedRepository.findByUserAndMovie(user, movie);
        
        // If the combination already exists, delete the existing record
        if (existingRecord != null) {
            justWatchedRepository.delete(existingRecord);
        }

        // Create and save the new JustWatched record
        JustWatched justWatched = new JustWatched();
        justWatched.setUser(user);
        justWatched.setMovie(movie);
        justWatched.setWatchDate(LocalDateTime.now());

        justWatchedRepository.save(justWatched);

        // Print system out values
        System.out.println("Added to just_watched - userId: " + userId + ", movieId: " + movieId);
    }

    @Override
    public List<JustWatched> getRecentWatchedMovies(int userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        return justWatchedRepository.findByUserOrderByWatchDateDesc(user);
    }
}