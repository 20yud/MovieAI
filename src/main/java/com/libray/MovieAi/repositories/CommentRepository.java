package com.libray.MovieAi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.libray.MovieAi.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByMovieId(int movieId);
}