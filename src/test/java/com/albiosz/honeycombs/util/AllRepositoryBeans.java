package com.albiosz.honeycombs.util;

import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.user.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class AllRepositoryBeans {

	@Bean
	public GameRepository gameRepository() {
		return mock(GameRepository.class);
	}

	@Bean
	public UserRepository userRepository() {
		return mock(UserRepository.class);
	}
}
