package com.libray.MovieAi.controllers;

import java.security.Principal;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.repositories.UserRepository;
import com.libray.MovieAi.services.FavoritesService;
import com.libray.MovieAi.services.MovieService;
import com.libray.MovieAi.services.MoviesRepository;
import com.libray.MovieAi.services.UserService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {
    
    @Autowired
    private final FavoritesService favoritesService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private MoviesRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @PostMapping("/like/{movieId}")
    public ResponseEntity<String> likeMovie(@PathVariable Integer movieId, Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        Optional<Movie> movie = movieRepository.findById(movieId);

        if (user != null && movie.isPresent()) {
            if (favoritesService.isFavorite(user, movie.get())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("You have already liked this movie.");
            }
            favoritesService.addFavorite(user, movie.get());
            return ResponseEntity.ok("Movie liked successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Movie not found.");
        }
    }

    @PostMapping("/unlike/{movieId}")
    public ResponseEntity<String> unlikeMovie(@PathVariable Integer movieId, Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        Optional<Movie> movie = movieRepository.findById(movieId);

        if (user != null && movie.isPresent()) {
            if (favoritesService.isFavorite(user, movie.get())) {
                favoritesService.removeFavorite(user, movie.get());
                return ResponseEntity.ok("Movie unliked successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("You haven't liked this movie yet.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Movie not found.");
        }
    }


}