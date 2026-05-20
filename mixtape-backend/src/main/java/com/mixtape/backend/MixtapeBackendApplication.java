package com.mixtape.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.mixtape")
@EntityScan(basePackages = "com.mixtape.model")
@EnableJpaRepositories(basePackages = "com.mixtape.repository")
public class MixtapeBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(MixtapeBackendApplication.class, args);
	}
}