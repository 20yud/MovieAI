package com.libray.MovieAi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.libray.MovieAi.models.Comment;
import com.libray.MovieAi.models.User;


public interface UserRepository extends JpaRepository<User, Integer> {
	
	User findByUsername(String username);
	
	
}
