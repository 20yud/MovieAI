package com.libray.MovieAi.services;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.libray.MovieAi.models.Genre;
import com.libray.MovieAi.models.Movie;

import jakarta.transaction.Transactional;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, Integer> {

    // Pagination
    @Query(value = "SELECT * FROM Movies LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Movie> findMoviesPerPage(@Param("limit") int limit, @Param("offset") int offset);

    // Random movies
    @Query(value = "SELECT * FROM Movies ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Movie> findRandomMovies(@Param("limit") int limit);

    // Search by title
    @Query(value = "SELECT * FROM Movies WHERE original_title LIKE %:title%", nativeQuery = true)
    List<Movie> findByTitleContainingIgnoreCaseLimited(@Param("title") String title);

    @Query(value = "SELECT COUNT(*) FROM Movies WHERE original_title LIKE %:title%", nativeQuery = true)
    int countByTitleContainingIgnoreCase(@Param("title") String title);

    // Top favorite films
    @Query(value = "SELECT * FROM Movies ORDER BY vote_average * vote_count DESC LIMIT :limit", nativeQuery = true)
    List<Movie> findTopFavoriteFilms(@Param("limit") int limit);

    // Latest films
    @Query(value = "SELECT * FROM Movies ORDER BY release_date DESC LIMIT :limit", nativeQuery = true)
    List<Movie> findLatestFilms(@Param("limit") int limit);

    // Top movies by vote average
    @Query(value = "SELECT * FROM Movies ORDER BY vote_average * vote_count DESC LIMIT :limit", nativeQuery = true)
    List<Movie> findTopMoviesByVoteAverage(@Param("limit") int limit);

    // Newest movies
    @Query(value = "SELECT * FROM Movies ORDER BY release_date DESC LIMIT :limit", nativeQuery = true)
    List<Movie> findNewestMovies(@Param("limit") int limit);

    // Distinct release years
    @Query(value = "SELECT DISTINCT YEAR(STR_TO_DATE(release_date, '%m/%d/%Y')) as year FROM Movies WHERE release_date IS NOT NULL ORDER BY year DESC", nativeQuery = true)
    List<Integer> findDistinctReleaseYears();

    // Find movies by genre
    @Query(value = "SELECT m.* FROM Movies m JOIN movie_genre mg ON m.id = mg.movie_id WHERE mg.genre_id = :genreId LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Movie> findMoviesByGenre(@Param("genreId") int genreId, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM Movies m JOIN movie_genre mg ON m.id = mg.movie_id WHERE mg.genre_id = :genreId", nativeQuery = true)
    int countMoviesByGenre(@Param("genreId") int genreId);

    // Find movies by year
    @Query(value = "SELECT * FROM Movies WHERE YEAR(STR_TO_DATE(release_date, '%m/%d/%Y')) = :year LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Movie> findMoviesByYear(@Param("year") int year, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM Movies WHERE YEAR(STR_TO_DATE(release_date, '%m/%d/%Y')) = :year", nativeQuery = true)
    int countMoviesByYear(@Param("year") int year);

    // Find movies by runtime greater than a value
    @Query(value = "SELECT * FROM Movies WHERE CAST(runtime AS UNSIGNED) > :runtime LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Movie> findMoviesByRuntimeGreaterThan(@Param("runtime") int runtime, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM Movies WHERE CAST(runtime AS UNSIGNED) > :runtime", nativeQuery = true)
    int countMoviesByRuntimeGreaterThan(@Param("runtime") int runtime);

    // Find movies by runtime less than a value
    @Query(value = "SELECT * FROM Movies WHERE CAST(runtime AS UNSIGNED) < :runtime LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Movie> findMoviesByRuntimeLessThan(@Param("runtime") int runtime, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM Movies WHERE CAST(runtime AS UNSIGNED) < :runtime", nativeQuery = true)
    int countMoviesByRuntimeLessThan(@Param("runtime") int runtime);

    // General movie count and find
    @Query(value = "SELECT m.* FROM Movies m LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Movie> findMovies(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM Movies", nativeQuery = true)
    int countAllMovies();

    // Find movies by runtime, year, and genre
    @Query(value = "SELECT * FROM Movies m JOIN movie_genre mg ON m.id = mg.movie_id WHERE YEAR(STR_TO_DATE(m.release_date, '%m/%d/%Y')) > :year AND CAST(m.runtime AS UNSIGNED) > :runtime AND mg.genre_id = :genreId LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Movie> findMoviesByRuntimeYearAndGenre(@Param("runtime") int runtime, @Param("year") int year, @Param("genreId") int genreId, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM Movies m JOIN movie_genre mg ON m.id = mg.movie_id WHERE YEAR(STR_TO_DATE(m.release_date, '%m/%d/%Y')) > :year AND CAST(m.runtime AS UNSIGNED) > :runtime AND mg.genre_id = :genreId", nativeQuery = true)
    int countMoviesByRuntimeYearAndGenre(@Param("runtime") int runtime, @Param("year") int year, @Param("genreId") int genreId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM movie_genre WHERE movie_id = :movieId", nativeQuery = true)
    void deleteMovieGenresByMovieId(@Param("movieId") int movieId);

    // Delete movie by id and associated genres
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Movies WHERE id = :movieId", nativeQuery = true)
    void deleteMovieAndAssociatedGenres(@Param("movieId") int movieId);
    
}