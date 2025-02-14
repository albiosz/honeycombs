package com.albiosz.honeycombs.integration;

import com.albiosz.honeycombs.HoneycombsApplication;
import com.albiosz.honeycombs.auth.dto.UserLoginDto;
import com.albiosz.honeycombs.auth.response.LoginResponse;
import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.game.State;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.albiosz.honeycombs.integration.AuthTest.createURLWithPort;
import static com.albiosz.honeycombs.integration.AuthTest.login;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(
		classes = HoneycombsApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
		}
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameTests {
	@LocalServerPort
	private int port;

	@MockitoBean
	private final GameRepository gameRepository = mock(GameRepository.class);

	@MockitoBean
	private final UserRepository userRepository = mock(UserRepository.class);

	private String jwtToken;

	@BeforeEach
	void setUp() {
		when(userRepository.findByUsername("email@email.com")).thenReturn(Optional.of(new User("email@email.com", new BCryptPasswordEncoder().encode("password"), "user", true)));
		jwtToken = loginAndGetToken();
	}

	@Test
	void getGame() {
		when(gameRepository.findById(1L)).thenReturn(Optional.of(new Game()));

		String url = createURLWithPort(port, "/api/game?id=1");

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		ResponseEntity<Game> response = new TestRestTemplate()
				.exchange(url, HttpMethod.GET, entity, Game.class);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(State.CREATED, Objects.requireNonNull(response.getBody()).getState());
	}

	private String loginAndGetToken() {
		String url = createURLWithPort(port, "/auth/login");
		UserLoginDto userLoginDto = new UserLoginDto("email@email.com", "password");
		ResponseEntity<LoginResponse> response = login(url, userLoginDto);
		assertEquals(200, response.getStatusCode().value());

		return Objects.requireNonNull(response.getBody()).getToken();
	}
}
