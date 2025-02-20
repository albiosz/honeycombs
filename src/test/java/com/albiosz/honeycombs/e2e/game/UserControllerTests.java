package com.albiosz.honeycombs.e2e.game;

import com.albiosz.honeycombs.HoneycombsApplication;
import com.albiosz.honeycombs.auth.JwtService;
import com.albiosz.honeycombs.auth.dto.UserLoginDto;
import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static com.albiosz.honeycombs.util.Auth.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = HoneycombsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserControllerTests {

	@LocalServerPort
	private int port;

	private TestRestTemplate restTemplate;
	private HttpHeaders headers;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	GameRepository gameRepository;

	@Autowired
	private JwtService jwtService;
	private String jwtToken;

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:16-alpine"
	)
			.withDatabaseName("honeycombs");



	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@DynamicPropertySource
	static void configureDatasource(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	private User user;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		user = userRepository.save(new User("email@email.com", new BCryptPasswordEncoder().encode("password"), "user", true));

		String url = createURLWithPort(port, "/auth/login");
		UserLoginDto userLoginDto = new UserLoginDto(user.getUsername(), "password");
		jwtToken = sendLoginRequest(url, userLoginDto).getBody().getToken();

		restTemplate = new TestRestTemplate();
		headers = new HttpHeaders();
	}

	@Test
	void testJoinGame() {
		Game game = gameRepository.save(new Game());

		String url = createURLWithPort(port, "/api/game/add-user/" + game.getId());
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(org.springframework.http.MediaType.APPLICATION_JSON));

		HttpEntity<?> entity = new HttpEntity<>(null, headers);

		ResponseEntity<Game> response = restTemplate.exchange(
				url,
				HttpMethod.PUT,
				entity,
				Game.class
		);
		Game responseGame = response.getBody();

		assertEquals(200, response.getStatusCode().value());
		assertEquals(1, responseGame.getUserGames().size());
		assertNotNull(responseGame.getUserGames().get(user.getId()).getUser());

		game = gameRepository.findById(game.getId()).orElseThrow();
		assertEquals(1, game.getUserGames().size());
	}
}
