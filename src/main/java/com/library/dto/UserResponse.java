package com.library.dto;

import com.library.model.User.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Lombok annotations for generating constructor, getters, setters, and builder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;               // Enum from User entity
    private String contactDetails;

    // No password property included here intentionally
}



