package com.libray.MovieAi.controllers;

import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.services.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MoviesRepository repo;

    @GetMapping({"", "/"})
    public String showMovieList(Model model, @RequestParam(required = false) String searchQuery,
                                @RequestParam(defaultValue = "1") int page) {
        int limit = 60; // Number of movies per page
        int offset = (page - 1) * limit; // Calculate offset based on the page number
        List<Movie> movies;
        int totalMovies;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            movies = repo.findByTitleContainingIgnoreCaseLimited(searchQuery);
            totalMovies = repo.countByTitleContainingIgnoreCase(searchQuery);
        } else {
            movies = repo.findMoviesPerPage(limit, offset);
            totalMovies = repo.countAllMovies();
        }

        int totalPages = (int) Math.ceil((double) totalMovies / limit);
        model.addAttribute("movies", movies);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // Fetch top favorite films based on voteAverage
        List<Movie> topFavoriteFilms = repo.findTopFavoriteFilms(6); // Assuming 6 as the limit
        model.addAttribute("topFavoriteFilms", topFavoriteFilms);
        
     // Fetch latest films
        List<Movie> latestFilms = repo.findLatestFilms(6); // Assuming 6 as the limit
        model.addAttribute("latestFilms", latestFilms);

        return "movies/index";
    }


    @GetMapping("/detail/{id}")
    public String showMovieDetail(@PathVariable("id") int id, Model model) {
        Movie movie = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
        model.addAttribute("movie", movie);
        return "movies/detailMovie";
    }

    @GetMapping("/random")
    public String getRandomMovies(@RequestParam int limit, Model model) {
        List<Movie> movies = repo.findRandomMovies(limit);
        model.addAttribute("movies", movies);
        return "movies/index";
    }

    @GetMapping("/faq")
    public String showFAQPage() {
        return "movies/faq";
    }
    
    @GetMapping("/search")
    public String searchMovies(@RequestParam(required = false) String query, Model model) {
        List<Movie> searchResults = null;
        if (query != null && !query.isEmpty()) {
            searchResults = repo.findByTitleContainingIgnoreCaseLimited(query);
        }
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query);
        return "movies/searchpage";
    }
    
 
}
