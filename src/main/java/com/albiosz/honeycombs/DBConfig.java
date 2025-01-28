package com.albiosz.honeycombs;

import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {

	@Bean
	CommandLineRunner setUpDatabase(
			UserRepository userRepository
	) {
		return args -> {
			User albert = new User("email@email.com", "password", "nickname");

			User createdUser = userRepository.save(albert);

			String email = createdUser.getEmail();
			User foundUser = userRepository.findById(createdUser.getId()).orElseThrow();
			System.out.println(foundUser);
		};
	}
}
