package com.library.service.impl;

import com.library.dto.UserDTO;
import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.library.constants.ErrorCode.USER_NOT_FOUND;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setContactDetails(userDTO.getContactDetails());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UserDTO userDTO){
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            logThrowUserNotFound(userId);
        }
        User user = userOpt.get();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setContactDetails(userDTO.getContactDetails());

        // If password provided and non-empty, update it (encode)
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return userRepository.save(user);
    }

    private static void logThrowUserNotFound(Long userId) {
        log.error("{} userid: {} ", USER_NOT_FOUND.getMessage(), userId);
        throw new RuntimeException(USER_NOT_FOUND.getMessage());
    }

    @Override
    public void deleteUser(Long userId){
        if(!userRepository.existsById(userId)){
            logThrowUserNotFound(userId);
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
