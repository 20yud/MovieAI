package com.libray.MovieAi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.libray.MovieAi.models.Comment;
import com.libray.MovieAi.models.CommentDto;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.Rating;
import com.libray.MovieAi.repositories.CommentRepository;
import com.libray.MovieAi.repositories.RatingRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MoviesRepository movieRepository;

    
    private UserService userService; // Inject UserService

    // Constructor for injecting UserService
    @Autowired
    public CommentServiceImpl(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    @Transactional
    public Comment addComment(CommentDto commentDto) {
        // Save the comment
    	
    	
        Comment comment = new Comment();
        comment.setUser(commentDto.getUser());

        // Fetch the Movie entity from the repository
        Movie movie = movieRepository.findById(commentDto.getMovieId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid movie ID"));

        comment.setMovie(movie);
        comment.setCommentText(commentDto.getCommentText());
        comment.setCommentDate(commentDto.getCommentDate() != null ? commentDto.getCommentDate() : LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        // Save the rating
        Rating rating = new Rating();
        rating.setUser(commentDto.getUser());
        rating.setMovie(movie);
        rating.setRating(commentDto.getRating());
        rating.setRatingDate(LocalDateTime.now());
        ratingRepository.save(rating);

        return savedComment;
    }

    @Override
    public List<Comment> getCommentsByMovieId(int movieId) {
        return commentRepository.findByMovieId(movieId);
    }
    
    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment getCommentById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
    }

    @Override
    public void updateComment(int id, Comment updatedComment) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        existingComment.setCommentText(updatedComment.getCommentText());
        existingComment.setCommentDate(updatedComment.getCommentDate());
        commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(int id) {
        commentRepository.deleteById(id);
    }
    
    
}