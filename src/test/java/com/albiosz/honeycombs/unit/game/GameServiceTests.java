package com.albiosz.honeycombs.unit.game;


import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameServiceTests {

	private UserService userService;

	@Mock
	private UserRepository userRepository = mock(UserRepository.class);

	private static final UUID uuid = UUID.randomUUID();
	private static final User user = new User("email@email.com", "password", "user", true);
	private static final long gameId = 1L;

	@BeforeEach
	void setUp() {
		user.setId(UUID.randomUUID());
		when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
		userService = new UserService(userRepository);
	}

	@Test
	void testGetUserById() {
		User foundUser = userService.getUserById(uuid);
		assertEquals(foundUser.getUsername(), user.getUsername());
	}
}
