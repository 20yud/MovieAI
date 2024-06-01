package com.libray.MovieAi.services;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
    
    
}
