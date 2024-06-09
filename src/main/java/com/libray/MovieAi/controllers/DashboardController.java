package com.libray.MovieAi.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.libray.MovieAi.models.Comment;
import com.libray.MovieAi.models.Genre;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.repositories.GenresRepository;
import com.libray.MovieAi.services.CommentService;
import com.libray.MovieAi.services.MovieService;
import com.libray.MovieAi.services.MoviesRepository;
import com.libray.MovieAi.services.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class DashboardController {
		
	 @Autowired
	    private MovieService movieService;
	  @Autowired
	    private UserService userService;
	    @Autowired
	    private MoviesRepository repo;
	    @Autowired
	    private GenresRepository genresRepository;
	
	    @Autowired
		 private CommentService commentService;
	    
	    @GetMapping("/test")
	    public String homeForm(Model model) {
	        int totalMovies = movieService.getTotalMovies();
	        Map<String, Integer> totalMoviesByGenre = movieService.getTotalMoviesByGenre();
	        int totalUsers = movieService.getTotalUsers();

	        List<String> genres = new ArrayList<>(totalMoviesByGenre.keySet());
	        List<Integer> counts = new ArrayList<>(totalMoviesByGenre.values());

	        model.addAttribute("totalMovies", totalMovies);
	        model.addAttribute("totalMoviesByGenre", totalMoviesByGenre);
	        model.addAttribute("genres", genres);
	        model.addAttribute("counts", counts);
	        model.addAttribute("totalUsers", totalUsers);

	        return "admin/statistics";
	    }
	 
	 
	    @GetMapping("/showcomments")
	    public String showComments(Model model) {
	        List<Comment> comments = commentService.getAllComments();
	        model.addAttribute("comments", comments);
	        return "admin/comments";
	    }

	    @GetMapping("/comments/view/{id}")
	    public String viewComment(@PathVariable int id, Model model) {
	        Comment comment = commentService.getCommentById(id);
	        model.addAttribute("comment", comment);
	        return "admin/viewComment";
	    }

	    @GetMapping("/comments/edit/{id}")
	    public String editCommentForm(@PathVariable int id, Model model) {
	        Comment comment = commentService.getCommentById(id);
	        model.addAttribute("comment", comment);
	        return "admin/editComment";
	    }

	    @PostMapping("/comments/edit/{id}")
	    public String editComment(@PathVariable int id, @ModelAttribute Comment comment) {
	        commentService.updateComment(id, comment);
	        return "redirect:/showcomments";
	    }

	    @GetMapping("/comments/delete/{id}")
	    public String deleteComment(@PathVariable int id) {
	        commentService.deleteComment(id);
	        return "redirect:/showcomments";
	    }
		 
	    
	    
	    
	
	
	 
	 
	 
	    @GetMapping("/showmovies")
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
	        model.addAttribute("searchQuery", searchQuery);

	        // Add user profile information if authenticated
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
	            String username = auth.getName(); // Get username of logged-in user
	            User user = userService.getUserByUsername(username); // Assuming you have a method to retrieve user by username
	            model.addAttribute("user", user);
	        } else {
	            model.addAttribute("user", null);
	        }
	        return "admin/movies";
	    }

	 @GetMapping("/movies/add")
	    public String showAddMovieForm(Model model) {
	        model.addAttribute("movie", new Movie());
	        model.addAttribute("genres", genresRepository.findAll());
	        return "admin/movies/create";
	    }

	 @PostMapping("/movies/add")
	 public String addMovie(@ModelAttribute @Valid Movie movie, 
	                        @RequestParam("genreIds") List<Integer> genreIds, 
	                        @RequestParam("posterFile") MultipartFile posterFile,
	                        RedirectAttributes redirectAttributes) {
	     try {
	    	 
	    	 // Parse the release date to the desired format
	         DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	         DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
	         LocalDate parsedDate = LocalDate.parse(movie.getReleaseDate(), inputFormatter);
	         String formattedDate = outputFormatter.format(parsedDate);
	         movie.setReleaseDate(formattedDate);
	         // First, save the movie to generate the ID
	         repo.save(movie);

	         // Save the poster file
	         String folder = "src/main/resources/static/images/posters/";
	         String pathname = "/" + movie.getImdbId() + movie.getId() + ".jpg";
	         String filename = movie.getImdbId() + movie.getId() + ".jpg" + ".jpg"; // Constructing the filename
	         byte[] bytes = posterFile.getBytes();
	         Path path = Paths.get(folder + filename);
	         Files.write(path, bytes);

	         // Set the poster path
	         movie.setPosterPath(pathname);

	         // Update the movie with the poster path
	         repo.save(movie);

	         // Set the genres for the movie
	         List<Genre> genres = genresRepository.findAllById(genreIds);
	         movie.setGenres(genres);

	         // Save the movie again to update genres
	         repo.save(movie);

	         redirectAttributes.addFlashAttribute("successMessage", "Movie added successfully!");
	     } catch (Exception e) {
	         e.printStackTrace();
	         redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while adding the movie.");
	     }
	     
	     return "redirect:/admin/showmovies";
	 }




	 @GetMapping("/movies/edit/{id}")
	 public String showEditMovieForm(@PathVariable Integer id, Model model) {
	     Optional<Movie> movieOptional = repo.findById(id);
	     if (movieOptional.isPresent()) {
	         Movie movie = movieOptional.get();
	         model.addAttribute("movie", movie);
	         model.addAttribute("genres", genresRepository.findAll());
	         return "admin/movies/edit";
	     } else {
	         return "redirect:/admin/showmovies";
	     }
	 }


	 @PostMapping("/movies/edit")
	 public String editMovie(@ModelAttribute @Valid Movie movie, 
	                         @RequestParam("genreIds") List<Integer> genreIds, 
	                         @RequestParam("posterFile") MultipartFile posterFile,
	                         RedirectAttributes redirectAttributes) {
	     try {
	         Optional<Movie> movieOptional = repo.findById(movie.getId());
	         if (movieOptional.isPresent()) {
	             Movie existingMovie = movieOptional.get();

	             // Update fields only if they have new values
	             if (Objects.nonNull(movie.getImdbId())) existingMovie.setImdbId(movie.getImdbId());
	             if (Objects.nonNull(movie.getOriginalTitle())) existingMovie.setOriginalTitle(movie.getOriginalTitle());
	             if (Objects.nonNull(movie.getOverview())) existingMovie.setOverview(movie.getOverview());
	             if (Objects.nonNull(movie.getPopularity())) existingMovie.setPopularity(movie.getPopularity());
	             if (Objects.nonNull(movie.getReleaseDate())) existingMovie.setReleaseDate(movie.getReleaseDate());
	             if (Objects.nonNull(movie.getRuntime())) existingMovie.setRuntime(movie.getRuntime());
	             if (Objects.nonNull(movie.getVoteAverage())) existingMovie.setVoteAverage(movie.getVoteAverage());
	             if (Objects.nonNull(movie.getVoteCount())) existingMovie.setVoteCount(movie.getVoteCount());

	             // Handle the poster file if it was uploaded
	             if (!posterFile.isEmpty()) {
	                 String folder = "src/main/resources/static/images/posters/";
	                 String pathname = "/" + existingMovie.getImdbId() + existingMovie.getId() + ".jpg";
	                 String filename = existingMovie.getImdbId() + existingMovie.getId() + ".jpg";
	                 byte[] bytes = posterFile.getBytes();
	                 Path path = Paths.get(folder + filename);
	                 Files.write(path, bytes);
	                 existingMovie.setPosterPath(pathname);
	             }

	             // Set the genres for the movie
	             List<Genre> genres = genresRepository.findAllById(genreIds);
	             existingMovie.setGenres(genres);

	             // Save the updated movie
	             repo.save(existingMovie);

	             redirectAttributes.addFlashAttribute("successMessage", "Movie updated successfully!");
	         } else {
	             redirectAttributes.addFlashAttribute("errorMessage", "Movie not found.");
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	         redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating the movie.");
	     }
	     return "redirect:/admin/showmovies";
	 }



	    @GetMapping("/delete/{id}")
	    public String deleteMovie(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
	        // Ensure the movie exists
	        Movie movie = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
	        
	        // Delete associated movie_genre records
	        repo.deleteMovieGenresByMovieId(id);
	        
	        // Delete the movie
	        repo.deleteById(id);
	        
	        // Add success message
	        redirectAttributes.addFlashAttribute("successMessage", "Movie deleted successfully!");
	        
	        return "redirect:/admin/showmovies";
	    }
	    
	    @GetMapping("/showusers")
		public String showAllUsers(Model model) {
		    List<User> users = userService.findAllUsers();
		    model.addAttribute("users", users);
		    return "admin/users";
		}
	}	