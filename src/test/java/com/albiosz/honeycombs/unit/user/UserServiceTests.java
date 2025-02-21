package com.albiosz.honeycombs.unit.user;


import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTests {

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

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				user,
				null,
				user.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	@Test
	void testGetUserById() {
		User foundUser = userService.getUserById(uuid);
		assertEquals(foundUser.getUsername(), user.getUsername());
	}

}
