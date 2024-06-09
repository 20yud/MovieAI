package com.libray.MovieAi.services;

import com.libray.MovieAi.models.Comment;
import com.libray.MovieAi.models.CommentDto;

import java.util.List;

public interface CommentService {
    Comment addComment(CommentDto commentDto);
    List<Comment> getCommentsByMovieId(int movieId);
    List<Comment> getAllComments();
    Comment getCommentById(int id);
    void updateComment(int id, Comment comment);
    void deleteComment(int id);
}