package com.libray.MovieAi.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.libray.MovieAi.models.JustWatched;
import com.libray.MovieAi.models.User;
import com.libray.MovieAi.models.UserDto;
import com.libray.MovieAi.repositories.GenresRepository;
import com.libray.MovieAi.services.JustWatchedService;
import com.libray.MovieAi.services.MoviesRepository;
import com.libray.MovieAi.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JustWatchedService justWatchedService;

    @GetMapping("/")
    public String home() {
        return "users/index";
    }

    @GetMapping("/home")
    public String homeuser() {
        return "users/home";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsernameWithFavorites(username);

        // Fetch all watched movies for the user
        List<JustWatched> justWatchedMovies = justWatchedService.getRecentWatchedMovies(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("justWatchedMovies", justWatchedMovies);
        return "profile";
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @Validated @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Perform logout
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // Redirect to the login page or any other desired page after logout
        return "redirect:/login";
    }
}
