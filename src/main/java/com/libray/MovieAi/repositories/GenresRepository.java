package com.libray.MovieAi.repositories;



import com.libray.MovieAi.models.Genre;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenresRepository extends JpaRepository<Genre, Integer> {
    List<Genre> findAll();
}
