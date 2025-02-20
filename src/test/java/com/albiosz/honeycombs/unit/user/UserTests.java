package com.albiosz.honeycombs.unit.user;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.usergame.State;
import com.albiosz.honeycombs.usergame.UserGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserTests {

	private User user;
	private Game game;
	private UserGame userGame1;
	private UserGame userGame2;

	@BeforeEach
	void setUp() {
		user = new User("test@example.com", "password", "nickname", true);
		user.setId(UUID.randomUUID());
		userGame1 = mock(UserGame.class);
		userGame2 = mock(UserGame.class);

		when(userGame1.getState()).thenReturn(State.IN_LOBBY);
		when(userGame2.getState()).thenReturn(State.IN_LOBBY);

		user.getUserGames().add(userGame1);
		user.getUserGames().add(userGame2);

		game = mock(Game.class);
		when(game.getUserGames()).thenReturn(new HashMap<>());
	}

	@Test
	void testLeaveOtherLobbies() {
		assertEquals(2, user.getUserGames().size());

		user.leaveOtherLobbies(game);

		verify(game, times(2)).getUserGames();
		assertEquals(0, user.getUserGames().size());
	}
}
