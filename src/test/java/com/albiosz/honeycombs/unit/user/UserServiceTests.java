package com.albiosz.honeycombs.unit.user;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.game.GameService;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTests {

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
		when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(new Game()));
		gameService = new GameService(gameRepository, userRepository);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				user,
				null,
				user.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	@Test
	void testAddUser() {
		Game game = gameService.addUser(gameId);
		assertTrue(game.getUserGames().size() > 0);
	}
}
