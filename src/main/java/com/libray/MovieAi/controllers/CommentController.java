package com.libray.MovieAi.controllers;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.libray.MovieAi.models.Comment;
import com.libray.MovieAi.models.CommentDto;
import com.libray.MovieAi.models.Rating;
import com.libray.MovieAi.models.RatingDto;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.models.UserDto;
import com.libray.MovieAi.services.CommentService;
import com.libray.MovieAi.services.RatingService;
import com.libray.MovieAi.services.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentDto commentDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            commentDto.setUser(user); // Set the User object directly
            Comment comment = commentService.addComment(commentDto);
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/fetchComments/{movieId}")
    public ResponseEntity<List<CommentDto>> fetchComments(@PathVariable int movieId) {
        List<Comment> comments = commentService.getCommentsByMovieId(movieId);
        List<Rating> ratings = ratingService.getRatingsByMovieId(movieId);
        List<CommentDto> commentDtos = new ArrayList<>();

        // Create a map to store ratings by date
        Map<LocalDate, Double> ratingMap = new HashMap<>();
        for (Rating rating : ratings) {
            ratingMap.put(rating.getRatingDate().toLocalDate(), rating.getRating());
        }

        for (Comment comment : comments) {
            CommentDto commentDto = new CommentDto();
            commentDto.setId(comment.getId());
            commentDto.setCommentText(comment.getCommentText());
            commentDto.setCommentDate(comment.getCommentDate());

            // Check if there is a rating for the comment's date
            Double ratingValue = ratingMap.get(comment.getCommentDate().toLocalDate());
            commentDto.setRating(ratingValue != null ? ratingValue : -1); // Set rating to -1 if not found

            // Populate user information
            UserDto userDto = new UserDto();
            userDto.setId(comment.getUser().getId());
            userDto.setUsername(comment.getUser().getUsername());
            commentDto.setUserDto(userDto);

            commentDtos.add(commentDto);
        }

        return ResponseEntity.ok(commentDtos);
    }


 // Inside your controller method where you handle rating submission
    @PostMapping("/ratings")
    public ResponseEntity<Rating> addRating(@RequestBody RatingDto ratingDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            
            // Set the user information in the RatingDto object
            ratingDto.setUser(user);
            
            // Call the addRating method in the service layer
            Rating rating = ratingService.addRating(ratingDto);
            return ResponseEntity.ok(rating);
        } else {
            // Handle unauthenticated user
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    
    
    
    @GetMapping("/fetchRatings/{movieId}")
    @ResponseBody
    public ResponseEntity<List<RatingDto>> fetchRatings(@PathVariable int movieId) {
        List<Rating> ratings = ratingService.getRatingsByMovieId(movieId);
        List<RatingDto> ratingDtos = new ArrayList<>();

        for (Rating rating : ratings) {
            // Create a simplified DTO object containing only necessary information
            RatingDto ratingDto = new RatingDto();
            ratingDto.setId(rating.getId());
            ratingDto.setUserId(rating.getUser().getId()); // Only include user ID
            ratingDto.setMovieId(rating.getMovie().getId()); // Only include movie ID
            ratingDto.setRating(rating.getRating());
            ratingDto.setRatingDate(rating.getRatingDate());

            ratingDtos.add(ratingDto);
        }

        return ResponseEntity.ok(ratingDtos);
    }
}