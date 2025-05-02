package com.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;

        @Column(unique = true, nullable = false)
        private String email;

        @Enumerated(EnumType.STRING)
        private Role role;
        private String contactDetails;

        // Relations
        @OneToMany(mappedBy = "user")
        private Set<BorrowRecord> borrowRecords;

        public enum Role {
            LIBRARIAN, PATRON
        }

}

