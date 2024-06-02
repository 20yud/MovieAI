package com.libray.MovieAi.controllers;

import com.libray.MovieAi.models.Genre;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.services.MovieService;
import com.libray.MovieAi.services.MoviesRepository;
import com.libray.MovieAi.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Configuration
@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MoviesRepository repo;
    
    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;
    
    @GetMapping("/watch/{id}")
    public String showWatchPage(@PathVariable("id") int id, Model model) {
        Movie movie = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
        String embedLink = "";
        if (id == 293660) {
            embedLink = "https://vip.opstream11.com/share/dc5d637ed5e62c36ecb73b654b05ba2a";
        } else if (id == 68718) {
            embedLink = "https://vip.opstream16.com/share/559cb990c9dffd8675f6bc2186971dc2";
        }else if (id == 118340) {
            embedLink = "https://vip.opstream11.com/share/26f5bd4aa64fdadf96152ca6e6408068";
        }else if (id == 49026) {
            embedLink = "https://vip.opstream16.com/share/d6139184e17e1909941ebb7bd7e4793d";
        }else if (id == 47964) {
            embedLink = "https://vip.opstream14.com/share/17dcdf4af5a32bce2d8b16b775dcfb84";
        }else if (id == 70160) {
            embedLink = "https://vip.opstream15.com/share/b73ce398c39f506af761d2277d853a92";
        }else if (id == 22970) {
            embedLink = "https://vip.opstream15.com/share/7e909d0e18cec1ad8ad9076be0b669c2";
        }else if (id == 37724) {
            embedLink = "https://vip.opstream15.com/share/fe70c36866add1572a8e2b96bfede7bf";
        }else if (id == 41154) {
            embedLink = "https://vip.opstream14.com/share/65b9eea6e1cc6bb9f0cd2a47751a186f";
        
        }else if (id == 68721) {
            embedLink = "https://vip.opstream16.com/share/814411c7a909ca15fc65a67b585ddd4d";
        
        }
        model.addAttribute("movie", movie);
        model.addAttribute("embedLink", embedLink);
        return "movies/watch";
    }


    

    @GetMapping("/detail/{id}")
    public String showMovieDetail(@PathVariable("id") int id, Model model) {
        Movie movie = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
        
        List<Genre> genres = movie.getGenres();

        // Add user profile information if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName(); // Get username of logged-in user
            User user = userService.getUserByUsername(username); // Assuming you have a method to retrieve user by username
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }
        
        // Call the Flask API to get similar movies
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:5555/api/overview-suggest?id=" + id;
        
        try {
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            if (response != null && response.containsKey("recommended_movies")) {
                List<Map<String, Object>> recommendedMovies = (List<Map<String, Object>>) response.get("recommended_movies");
                System.out.println("Recommended Movies: " + recommendedMovies);
                model.addAttribute("recommendedMovies", recommendedMovies);
            } else {
                System.out.println("No recommended movies found in the response.");
                model.addAttribute("recommendedMovies", List.of()); // Add an empty list if no recommendations
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("recommendedMovies", List.of()); // Add an empty list if there is an error
        }
        String genreApiUrl = "http://localhost:5555/api/genre-suggest?id=" + id;
        try {
            Map<String, Object> genreResponse = restTemplate.getForObject(genreApiUrl, Map.class);
            if (genreResponse != null && genreResponse.containsKey("recommended_movies")) {
                List<Map<String, Object>> genreRecommendedMovies = (List<Map<String, Object>>) genreResponse.get("recommended_movies");
                System.out.println("Genre-based Recommended Movies: " + genreRecommendedMovies);
                model.addAttribute("genreRecommendedMovies", genreRecommendedMovies);
            } else {
                System.out.println("No genre-based recommended movies found in the response.");
                model.addAttribute("genreRecommendedMovies", List.of()); // Add an empty list if no recommendations
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("genreRecommendedMovies", List.of()); // Add an empty list if there is an error
        }
        model.addAttribute("movie", movie);
        model.addAttribute("genres", genres);
        return "movies/detailMovie";
    }


    @GetMapping("/filter")
    public String filterMovies(Model model, @RequestParam(defaultValue = "1") int page,
                               @RequestParam(required = false) String genre,
                               @RequestParam(required = false) Integer year,
                               @RequestParam(required = false) String duration) {
        int limit = 60; // Number of movies per page
        int offset = (page - 1) * limit; // Calculate offset based on the page number

        int runtimeThreshold = 90; // Minimum runtime in minutes
        int releaseYearThreshold = 2015; // Minimum release year

        List<Movie> movies;
        int totalMovies;

        // Check which parameter is not null and apply filtering accordingly
        if (genre != null && !genre.isEmpty()) {
            // Filter by genre
            movies = repo.findMoviesByGenre(genre, limit, offset);
            totalMovies = repo.countMoviesByGenre(genre);
        } else if (year != null) {
            // Filter by year
            movies = repo.findMoviesByYear(year, limit, offset);
            totalMovies = repo.countMoviesByYear(year);
        } else if (duration != null && !duration.isEmpty()) {
            // Filter by duration
            int runtimeValue = Integer.parseInt(duration.substring(1, duration.length() - 1));
            movies = repo.findMoviesByRuntime(runtimeValue, limit, offset);
            totalMovies = repo.countMoviesByRuntime(runtimeValue);
        } else {
        	// Default filtering by runtime threshold and release year threshold
        	movies = repo.findMoviesByRuntimeYearAndGenre(runtimeThreshold, releaseYearThreshold, genre, limit, offset);
        	totalMovies = repo.countMoviesByRuntimeYearAndGenre(runtimeThreshold, releaseYearThreshold, genre);

        }

        int totalPages = (int) Math.ceil((double) totalMovies / limit);
        model.addAttribute("filteredMovies", movies);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "movies/filter";
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
    public String showSearchPage(Model model, @RequestParam(required = false) String query) {
        List<Movie> searchResults = new ArrayList<>();
        if (query != null && !query.isEmpty()) {
            searchResults = repo.findByTitleContainingIgnoreCaseLimited(query);
            // Limit the number of search results to 60
            if (searchResults.size() > 60) {
                searchResults = searchResults.subList(0, 60);
            }
        }
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query); // Pass the query parameter to the view
        return "movies/searchpage";
    }

    @GetMapping("/top")
    public String showTopMovies(Model model) {
        List<Movie> topMovies = repo.findTopMoviesByVoteAverage(60); // Assuming 60 as the limit
        model.addAttribute("topMovies", topMovies);
        return "movies/topmovies";
    }

    @GetMapping("/newest")
    public String showNewestMovies(Model model) {
        List<Movie> newestMovies = repo.findNewestMovies(60); // Assuming 60 as the limit
        model.addAttribute("newestMovies", newestMovies);
        return "movies/newestmovies";
    }

}
