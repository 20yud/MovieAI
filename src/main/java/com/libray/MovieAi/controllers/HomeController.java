package com.libray.MovieAi.controllers;


import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.libray.MovieAi.models.Genre;
import com.libray.MovieAi.models.Movie;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.models.UserDto;
import com.libray.MovieAi.repositories.GenresRepository;
import com.libray.MovieAi.services.MoviesRepository;
import com.libray.MovieAi.services.UserService;

import jakarta.validation.Valid;



@Controller
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private MoviesRepository repo;
    @Autowired
    private GenresRepository genresRepository;
    
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

        // Fetch all genres
        List<Genre> genres = genresRepository.findAll();
        model.addAttribute("genres", genres);

        // Fetch distinct release years
        List<Integer> releaseYears = repo.findDistinctReleaseYears();
        model.addAttribute("releaseYears", releaseYears);

        // Add user profile information if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName(); // Get username of logged-in user
            User user = userService.getUserByUsername(username); // Assuming you have a method to retrieve user by username
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }

        return "index";
    }

    


    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
    
    @GetMapping("/forgetpwd")
    public String forgetpasswordPage() {
        return "forgetpassword";
    }
    
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        // Print notice that the method is called
        System.out.println("Checking email existence for: " + email);
        
        boolean emailExists = userService.checkEmailExists(email);
        if (emailExists) {
            // Email exists, generate a new password
            String newPassword = userService.generateNewPassword();

            // Send the new password to the email account
            userService.sendNewPasswordEmail(email, newPassword);

            // Update the new password in the database
            userService.updatePasswordByEmail(email, newPassword);

            // Return success response
            return ResponseEntity.ok().build();
        } else {
            // Email doesn't exist, return error response
            return ResponseEntity.badRequest().body("Email does not exist");
        }
    }

    
    
    
    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username, 
                            @RequestParam("password") String password,
                            RedirectAttributes redirectAttributes) {
        // Check if username and password are valid (e.g., authenticate against a database)
        boolean isValidUser = userService.authenticateUser(username, password);
        
        if (isValidUser) {
            // Redirect to the home page for authenticated users
            return "redirect:/movies";
        } else {
            // Redirect back to login page with an error message
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userDto") @Valid UserDto userDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            // Check if username or email already exists
            if (userService.checkUsernameExists(userDto.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại: " + userDto.getUsername());
                return "redirect:/register";
            }

            if (userService.checkEmailExists(userDto.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng");
                return "redirect:/register";
            }

            // If everything is fine, proceed to create user
            userService.createUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "User registered successfully!");
            return "redirect:/login"; // Redirect to login page after successful registration
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
   
    @GetMapping("/sideabar")
    public String showSidebarPage() {
        return "sidebar"; // Assuming your HTML file is named "dashboard.html" and it's under "admin" directory
    }
    
    @GetMapping("/error")
    public String showErrorPage() {
        return "error"; // Assuming your HTML file is named "dashboard.html" and it's under "admin" directory
    }
   

   
}
