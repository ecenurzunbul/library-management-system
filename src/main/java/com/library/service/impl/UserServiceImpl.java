package com.library.service.impl;

import com.library.dto.UserDTO;
import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
     
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setContactDetails(userDTO.getContactDetails());
        // Add password encoding, validation here

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UserDTO userDTO){
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            throw new RuntimeException("User not found with id: "+ userId);
        }
        User user = userOpt.get();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setContactDetails(userDTO.getContactDetails());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId){
        if(!userRepository.existsById(userId)){
            throw new RuntimeException("User not found with id: "+ userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<User> getUserById(Long userId){
        return userRepository.findById(userId);
    }

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
