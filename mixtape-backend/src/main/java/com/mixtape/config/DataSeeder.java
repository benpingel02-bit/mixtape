package com.mixtape.config;

import com.mixtape.model.User;
import com.mixtape.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() > 0) return;

            userRepository.save(new User("testuser", "test@mixtape.de",
                    passwordEncoder.encode("password123")));

            System.out.println("DataSeeder: Test-User angelegt (ID=1)");
        };
    }
}