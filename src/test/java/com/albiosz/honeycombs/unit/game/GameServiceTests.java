package com.albiosz.honeycombs.unit.game;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.game.GameService;
import com.albiosz.honeycombs.game.State;
import com.albiosz.honeycombs.game.exceptions.GameNotModifiable;
import com.albiosz.honeycombs.game.exceptions.UserNotGameHost;
import com.albiosz.honeycombs.game.exceptions.UserNotInGame;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameServiceTests {

	private GameService gameService;

	@Mock
	private UserRepository userRepository = mock(UserRepository.class);

	@Mock
	private GameRepository gameRepository = mock(GameRepository.class);

	private static final UUID uuid = UUID.randomUUID();
	private static final User user = new User("email@email.com", "password", "user", true);
	private static final long gameId = 1L;

	@BeforeEach
	void setUp() {
		user.setId(uuid);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				user,
				null,
				user.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	@Test
	@DisplayName("Game in Progress cannot be deleted!")
	void testDeleteGameById_GameInProgress() {
		Game game = new Game();
		game.setState(State.IN_PROGRESS);
		when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

		gameService = new GameService(gameRepository, userRepository);

		assertThrows(GameNotModifiable.class, () -> gameService.deleteGameById(gameId));
	}

	@Test
	@DisplayName("Game cannot be deleted if user is not in the game!")
	void testDeleteGameById_UserNotPartOfGame() {
		Game game = new Game();
		when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

		gameService = new GameService(gameRepository, userRepository);

		assertThrows(UserNotInGame.class, () -> gameService.deleteGameById(gameId));
	}

	@Test
	@DisplayName("Game cannot be deleted if user is not host of the game!")
	void testDeleteGameById_UserNotHost() {
		Game game = new Game();
		user.joinGame(game, false);
		when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

		gameService = new GameService(gameRepository, userRepository);

		assertThrows(UserNotGameHost.class, () -> gameService.deleteGameById(gameId));
	}

	@Test
	@DisplayName("Game can be successfully deleted!")
	void testDeleteGameById_gameCanBeDeleted() {
		Game game = new Game();
		user.joinGame(game, true);
		when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

		gameService = new GameService(gameRepository, userRepository);

		assertDoesNotThrow(() -> gameService.deleteGameById(gameId));
	}

	@Test
	void testAddUser() {
		when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(new Game()));
		gameService = new GameService(gameRepository, userRepository);

		Game game = gameService.addUser(gameId);
		assertEquals(1, game.getUserGames().size());
	}
}
