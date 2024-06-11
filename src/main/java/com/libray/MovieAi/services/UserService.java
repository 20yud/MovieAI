package com.libray.MovieAi.services;


import java.util.List;

import com.libray.MovieAi.models.User;
import com.libray.MovieAi.models.UserDto;

public interface UserService {
	 User createUser(UserDto userDto);
	    User getUserById(int id);
	    User updateUser(int id, UserDto userDto);
	    void deleteUser(int id);
	    List<User> findAllUsers();
	    boolean authenticateUser(String username, String password);
	    User getUserByUsername(String username);
	    User getUserByUsernameWithFavorites(String username);  // Add this method
	    boolean checkEmailExists(String email);
	    String generateNewPassword();
	    void updatePasswordByEmail(String email, String newPassword);
	    void sendNewPasswordEmail(String toEmail, String newPassword);
	    
}
