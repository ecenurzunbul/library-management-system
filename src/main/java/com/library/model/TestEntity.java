package com.library.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestEntity {
    @Id
    private Long id;
    private String message;

    // getters and setters
}


