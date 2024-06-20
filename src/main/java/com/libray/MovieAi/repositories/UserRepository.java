package com.libray.MovieAi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.libray.MovieAi.models.Comment;
import com.libray.MovieAi.models.User;


public interface UserRepository extends JpaRepository<User, Integer> {
	
	User findByUsername(String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favorites f LEFT JOIN FETCH f.movie WHERE u.username = :username")
    User findByUsernameWithFavorites(@Param("username") String username);

    boolean existsByEmail(String email); // Check if email exists
    
    User findByEmail(String email); // Find user by email
	    
	    
}
