package com.library.config;

import com.library.model.User;
import com.library.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {


   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;


   public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
       this.userRepository = userRepository;
       this.passwordEncoder = passwordEncoder;
   }


   @Override
   public void run(String... args) {
       try {
           boolean adminExists = userRepository.findByEmail("admin@library.com").isPresent();
           if (!adminExists) {
               User admin = User.builder()
                       .email("admin@library.com")
                       .name("Admin")
                       .role(User.Role.LIBRARIAN)
                       .contactDetails("Istanbul, Turkey")
                       .password(passwordEncoder.encode("test123"))
                       .build();


               userRepository.save(admin);
               log.info("Admin user created with email: {}", admin.getEmail());
           } else {
               log.info("Admin user already exists. No action taken.");
           }
       } catch (Exception e) {
           log.error("Exception in DataInitializer", e);
       }
   }


}
