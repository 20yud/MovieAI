package com.libray.MovieAi.services;


import java.util.List;

import com.libray.MovieAi.models.User;
import com.libray.MovieAi.models.UserDto;

public interface UserService {
    User createUser(UserDto userDto);
    User getUserById(int id);
    User updateUser(int id, UserDto userDto);
    void deleteUser(int id);
    List<User> findAllUsers();  // Add this method
  
	boolean authenticateUser(String username, String password);
	User getUserByUsername(String username);

}
