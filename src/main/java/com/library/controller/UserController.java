package com.library.controller;

import com.library.dto.UserDTO;
import com.library.model.User;
import com.library.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@Validated
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    // Register new user
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO){
        User newUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(UserDTO.toDTO(newUser), HttpStatus.CREATED);
    }

    // Get user details by ID
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        Optional<User> userOpt = userService.getUserById(id);
        if(userOpt.isPresent()){
            UserDTO userDTO = UserDTO.toDTO(userOpt.get());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // List all users
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        users.forEach(user -> userDTOs.add(UserDTO.toDTO(user)));
        return ResponseEntity.ok(userDTOs);
    }

    // Update user info
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @Valid @RequestBody UserDTO userDTO){
        try {
            User updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(UserDTO.toDTO(updatedUser));
        } catch (RuntimeException e){
            log.error("Error updating user with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        try{
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            log.error("Error deleting user with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
