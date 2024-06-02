package com.libray.MovieAi.services;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.libray.MovieAi.models.Genre;
import com.libray.MovieAi.models.Movie;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, Integer> {
	
	  @Query(value = "SELECT * FROM Movies LIMIT :limit OFFSET :offset", nativeQuery = true)
	    List<Movie> findMoviesPerPage(int limit, int offset);

	    @Query(value = "SELECT * FROM Movies ORDER BY RAND() LIMIT :limit", nativeQuery = true)
	    List<Movie> findRandomMovies(int limit);

	    @Query(value = "SELECT * FROM Movies WHERE original_title LIKE %:title%", nativeQuery = true)
	    List<Movie> findByTitleContainingIgnoreCaseLimited(@Param("title") String title);


	    @Query(value = "SELECT COUNT(*) FROM Movies WHERE original_title LIKE %:title%", nativeQuery = true)
	    int countByTitleContainingIgnoreCase(String title);

	    @Query(value = "SELECT COUNT(*) FROM Movies", nativeQuery = true)
	    int countAllMovies();

	    @Query(value = "SELECT * FROM Movies ORDER BY vote_average * vote_count DESC LIMIT :limit", nativeQuery = true)
	    List<Movie> findTopFavoriteFilms(@Param("limit") int limit);

	    
	    @Query(value = "SELECT * FROM Movies ORDER BY release_date DESC LIMIT :limit", nativeQuery = true)
	    List<Movie> findLatestFilms(int limit);
	    
	    @Query(value = "SELECT * FROM Movies ORDER BY vote_average * vote_count DESC LIMIT :limit", nativeQuery = true)
	    List<Movie> findTopMoviesByVoteAverage(int limit);

	    @Query(value = "SELECT * FROM Movies ORDER BY release_date DESC LIMIT :limit", nativeQuery = true)
	    List<Movie> findNewestMovies(int limit);
	    
	    @Query(value = "SELECT DISTINCT YEAR(STR_TO_DATE(release_date, '%m/%d/%Y')) as year FROM Movies WHERE release_date IS NOT NULL ORDER BY year DESC", nativeQuery = true)
	    List<Integer> findDistinctReleaseYears();
    
	    @Query(value = "SELECT * FROM Movies WHERE genre = :genre", nativeQuery = true)
	    List<Movie> findByGenre(@Param("genre") String genre);
	    
	    @Query(value = "SELECT * FROM Movies WHERE genre = :genre LIMIT :limit OFFSET :offset", nativeQuery = true)
	    List<Movie> findMoviesByGenre(@Param("genre") String genre, @Param("limit") int limit, @Param("offset") int offset);

	    @Query(value = "SELECT COUNT(*) FROM Movies WHERE genre = :genre", nativeQuery = true)
	    int countMoviesByGenre(@Param("genre") String genre);

	    @Query(value = "SELECT * FROM Movies WHERE YEAR(STR_TO_DATE(release_date, '%m/%d/%Y')) = :year LIMIT :limit OFFSET :offset", nativeQuery = true)
	    List<Movie> findMoviesByYear(@Param("year") int year, @Param("limit") int limit, @Param("offset") int offset);

	    @Query(value = "SELECT COUNT(*) FROM Movies WHERE YEAR(STR_TO_DATE(release_date, '%m/%d/%Y')) = :year", nativeQuery = true)
	    int countMoviesByYear(@Param("year") int year);

	    @Query(value = "SELECT * FROM Movies WHERE CAST(runtime AS UNSIGNED) > :runtime LIMIT :limit OFFSET :offset", nativeQuery = true)
	    List<Movie> findMoviesByRuntime(@Param("runtime") int runtime, @Param("limit") int limit, @Param("offset") int offset);

	    @Query(value = "SELECT COUNT(*) FROM Movies WHERE CAST(runtime AS UNSIGNED) > :runtime", nativeQuery = true)
	    int countMoviesByRuntime(@Param("runtime") int runtime);

	    @Query(value = "SELECT * FROM Movies WHERE YEAR(STR_TO_DATE(release_date, '%m/%d/%Y')) > :year AND CAST(runtime AS UNSIGNED) > :runtime AND genre = :genre LIMIT :limit OFFSET :offset", nativeQuery = true)
	    List<Movie> findMoviesByRuntimeYearAndGenre(@Param("runtime") int runtime, @Param("year") int year, @Param("genre") String genre, @Param("limit") int limit, @Param("offset") int offset);

	    @Query(value = "SELECT COUNT(*) FROM Movies WHERE YEAR(STR_TO_DATE(release_date, '%m/%d/%Y')) > :year AND CAST(runtime AS UNSIGNED) > :runtime AND genre = :genre", nativeQuery = true)
	    int countMoviesByRuntimeYearAndGenre(@Param("runtime") int runtime, @Param("year") int year, @Param("genre") String genre);


	    
    
}
