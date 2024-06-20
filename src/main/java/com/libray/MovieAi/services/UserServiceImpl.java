package com.libray.MovieAi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.libray.MovieAi.models.User;
import com.libray.MovieAi.models.UserDto;
import com.libray.MovieAi.repositories.UserRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JavaMailSender emailSender; // Autowire JavaMailSender

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại: " + userDto.getUsername());
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng: " + userDto.getEmail());
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        user.setJoinDate(LocalDateTime.now());

        return userRepository.save(user);
    }
    
    

    @Override
    public boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserByUsernameWithFavorites(String username) {
        return userRepository.findByUsernameWithFavorites(username);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User updateUser(int id, UserDto userDto) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Check if updated username or email already exists (excluding current user)
            if (!user.getUsername().equals(userDto.getUsername()) && userRepository.existsByUsername(userDto.getUsername())) {
                throw new RuntimeException("Username already exists: " + userDto.getUsername());
            }
            if (!user.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("Email already exists: " + userDto.getEmail());
            }
            
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            user.setRole(userDto.getRole());
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public String generateNewPassword() {
        Random random = new Random();
        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            newPassword.append(digit);
        }
        String generatedPassword = newPassword.toString();
        System.out.println("Generated new password: " + generatedPassword); // Print the new password to the console
        return generatedPassword;
    }

    @Override
    public void updatePasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }
    
    @Override
    public void sendNewPasswordEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Password");
        message.setText("Your new password is: " + newPassword);
        emailSender.send(message);
    }
}