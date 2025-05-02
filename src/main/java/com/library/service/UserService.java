package com.library.service;

import com.library.dto.UserDTO;
import com.library.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserDTO userDTO);
    
    User updateUser(Long userId, UserDTO userDTO);
    
    void deleteUser(Long userId);
    
    Optional<User> getUserById(Long userId);
    
    List<User> getAllUsers();
}
