package com.albiosz.honeycombs;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {

	@Bean
	CommandLineRunner setUpDatabase(
			UserRepository userRepo,
			GameRepository gameRepo
	) {
		return args -> {
			User albert = new User("email@email.com", "password", "nickname");

			User createdUser = userRepo.save(albert);
			System.out.println(createdUser);

			Game game = new Game();

			Game createdGame = gameRepo.save(game);

			System.out.println(createdGame);
		};
	}
}
