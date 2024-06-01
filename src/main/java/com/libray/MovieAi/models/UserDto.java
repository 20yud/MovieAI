package com.libray.MovieAi.models;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDto {

    private int id;

    @NotEmpty(message = "The username is required")
    @Size(max = 50, message = "The username cannot be more than 50 characters")
    private String username;

    @NotEmpty(message = "The email is required")
    @Email(message = "The email should be valid")
    @Size(max = 100, message = "The email cannot be more than 100 characters")
    private String email;

    @NotEmpty(message = "The password is required")
    @Size(min = 6, message = "The password should be at least 6 characters")
    @Size(max = 100, message = "The password cannot be more than 100 characters")
    private String password;
    
    @NotEmpty(message = "The role is required")
    @Size(max = 20, message = "The role cannot be more than 20 characters")
    private String role = "USER"; // Default role is set to "USER"

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
