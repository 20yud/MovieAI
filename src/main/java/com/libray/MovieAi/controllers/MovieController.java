package com.libray.MovieAi.controllers;

import com.libray.MovieAi.models.Genre;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.services.MovieService;
import com.libray.MovieAi.services.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
@Configuration
@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MoviesRepository repo;
    
    @Autowired
    private MovieService movieService;
    
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
        System.out.println("Genres: " + genres); // Add this line to print the genres
        model.addAttribute("movie", movie);
        model.addAttribute("genres", genres);
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
