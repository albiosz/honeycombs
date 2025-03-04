package com.albiosz.honeycombs.unit.auth;

import com.albiosz.honeycombs.auth.AuthService;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import com.albiosz.honeycombs.auth.dto.UserLoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

	private UserRepository userRepositoryMock;
	private AuthenticationManager authenticationManager;

	@BeforeEach
	void setUp() {
		User user = new User("user", "password", "nickname", true);

		userRepositoryMock = mock(UserRepository.class);
		when(userRepositoryMock.findByUsername("user")).thenReturn(Optional.of(user));

		authenticationManager = mock(AuthenticationManager.class);
		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "password"))).thenReturn(null);
	}

	@Test
	void testLogin() {
		AuthService authService = new AuthService(userRepositoryMock, null, authenticationManager, null);
		User returnedUser = authService.login(new UserLoginRequest("user", "password"));

		assert(returnedUser.getUsername().equals("user"));
		assert(returnedUser.getPassword().equals("password"));

	}
}
