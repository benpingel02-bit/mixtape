package com.mixtape.config;

import com.mixtape.model.User;
import com.mixtape.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() > 0) return;

            userRepository.save(new User("testuser", "test@mixtape.de", "placeholder"));

            System.out.println("DataSeeder: Test-User angelegt (ID=1)");
        };
    }
}