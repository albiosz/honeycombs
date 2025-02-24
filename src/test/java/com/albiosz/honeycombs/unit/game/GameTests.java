package com.albiosz.honeycombs.unit.game;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.usergame.UserGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameTests {

	private Game game;
	private User user;

	@BeforeEach
	void setUp() {
		game = new Game("new Game");
		game.setId(1L);
		user = new User("email@email.com", "password", "username", true);
		user.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

		game.getUserGames().put(user.getId(), new UserGame(user, game, true));
	}

	@Test
	@DisplayName("UserGame should be found")
	void testGetUserGame() {
		Optional<UserGame> userGame = game.getUserGame(user.getId());
		assertTrue(userGame.isPresent());
		assertEquals(user.getId(), userGame.get().getUser().getId());
	}

	@Test
	@DisplayName("UserGame should not be found")
	void testGetUserGameNotFound() {
		Optional<UserGame> userGame = game.getUserGame(UUID.randomUUID());
		assertTrue(userGame.isEmpty());
	}
}
