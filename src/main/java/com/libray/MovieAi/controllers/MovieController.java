package com.libray.MovieAi.controllers;

import com.libray.MovieAi.models.Genre;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.services.JustWatchedService;
import com.libray.MovieAi.services.MovieService;
import com.libray.MovieAi.services.MoviesRepository;
import com.libray.MovieAi.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
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
    
    @Autowired
    private JustWatchedService justWatchedService;
    
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
        
     // Add user profile information if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName(); // Get username of logged-in user
            User user = userService.getUserByUsername(username); // Assuming you have a method to retrieve user by username
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }
        model.addAttribute("movie", movie);
        model.addAttribute("embedLink", embedLink);
        return "movies/watch";
    }

    @GetMapping("/justwatched/{id}")
    public String addJustWatched(@PathVariable("id") int id) {
        // Retrieve user_id from authenticated session
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("User must be authenticated to perform this action");
        }

        String username = auth.getName(); // Get username of logged-in user
        User user = userService.getUserByUsername(username); // Retrieve user entity
        int userId = user.getId(); // Get user ID

        // System print to show userId and movieId
        System.out.println("Adding to just_watched - userId: " + userId + ", movieId: " + id);

        // Call JustWatchedService to add to JustWatched table
        justWatchedService.addJustWatched(userId, id);

        return "redirect:/movies/watch/" + id;
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
     // Java Code: Handling model attributes and API calls
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

        String genrecApiUrl = "http://localhost:5555/api/movie-recommendations?id=" + id;
        try {
            Map<String, Object> genreResponse = restTemplate.getForObject(genrecApiUrl, Map.class);
            if (genreResponse != null && genreResponse.containsKey("recommended_movies")) {
                List<Map<String, Object>> grecommendermodel = (List<Map<String, Object>>) genreResponse.get("recommended_movies");
                System.out.println("Genre-based Recommended Movies: " + grecommendermodel);
                model.addAttribute("grecommendermodel", grecommendermodel);
            } else {
                System.out.println("No genre-based recommended movies found in the response.");
                model.addAttribute("grecommendermodel", List.of());
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("grecommendermodel", List.of());
        }


        model.addAttribute("movie", movie);
        model.addAttribute("genres", genres);
        return "movies/detailMovie";
    }


    @GetMapping("/filter")
    public String filterMovies(Model model, @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String duration) {
int limit = 60; // Number of movies per page
int offset = (page - 1) * limit; // Calculate offset based on the page number

List<Movie> movies;
int totalMovies;

if (genre != null) {
movies = repo.findMoviesByGenre(genre, limit, offset);
totalMovies = repo.countMoviesByGenre(genre);
} else if (year != null) {
movies = repo.findMoviesByYear(year, limit, offset);
totalMovies = repo.countMoviesByYear(year);
} else if (duration != null && !duration.isEmpty()) {
// Extract the numeric value from the duration string
int runtimeValue = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
// Determine whether the duration should be greater than or less than the specified value
if (duration.startsWith(">")) {
movies = repo.findMoviesByRuntimeGreaterThan(runtimeValue, limit, offset);
totalMovies = repo.countMoviesByRuntimeGreaterThan(runtimeValue);
} else if (duration.startsWith("<")) {
movies = repo.findMoviesByRuntimeLessThan(runtimeValue, limit, offset);
totalMovies = repo.countMoviesByRuntimeLessThan(runtimeValue);
} else {
// Default behavior if the duration format is incorrect
movies = repo.findMovies(limit, offset);
totalMovies = repo.countAllMovies();
}
} else {
movies = repo.findMovies(limit, offset);
totalMovies = repo.countAllMovies();
}

int totalPages = (int) Math.ceil((double) totalMovies / limit);
model.addAttribute("filteredMovies", movies);
model.addAttribute("currentPage", page);
model.addAttribute("totalPages", totalPages);

return "movies/filter"; // Ensure this matches the actual template file
}


   


    @GetMapping("/random")
    public String getRandomMovies(@RequestParam int limit, Model model) {
        List<Movie> movies = repo.findRandomMovies(limit);
        model.addAttribute("movies", movies);
        return "movies/index";
    }

    @GetMapping("/faq")
    public String showFAQPage(Model model) {
    	 // Add user profile information if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName(); // Get username of logged-in user
            User user = userService.getUserByUsername(username); // Assuming you have a method to retrieve user by username
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }
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
        // Add user profile information if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName(); // Get username of logged-in user
            User user = userService.getUserByUsername(username); // Assuming you have a method to retrieve user by username
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query); // Pass the query parameter to the view
        return "movies/searchpage";
    }

    @GetMapping("/top")
    public String showTopMovies(Model model) {
        List<Movie> topMovies = repo.findTopMoviesByVoteAverage(60);
        // Add user profile information if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName(); // Get username of logged-in user
            User user = userService.getUserByUsername(username); // Assuming you have a method to retrieve user by username
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }// Assuming 60 as the limit
        model.addAttribute("topMovies", topMovies);
        return "movies/topmovies";
    }

    @GetMapping("/newest")
    public String showNewestMovies(Model model) {
        List<Movie> newestMovies = repo.findNewestMovies(60);
        // Add user profile information if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName(); // Get username of logged-in user
            User user = userService.getUserByUsername(username); // Assuming you have a method to retrieve user by username
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }// Assuming 60 as the limit
        model.addAttribute("newestMovies", newestMovies);
        return "movies/newestmovies";
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Perform logout
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // Redirect to the login page or any other desired page after logout
        return "redirect:/login";
    }
    
    @GetMapping("/detail/logout")
    public String logoutt(HttpServletRequest request, HttpServletResponse response) {
        // Perform logout
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // Redirect to the login page or any other desired page after logout
        return "redirect:/login";
    }
    
    
  
}
    


