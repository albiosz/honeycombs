package com.albiosz.honeycombs.e2e.game;

import com.albiosz.honeycombs.HoneycombsApplication;
import com.albiosz.honeycombs.auth.dto.UserLoginDto;
import com.albiosz.honeycombs.auth.response.LoginResponse;
import com.albiosz.honeycombs.config.exceptions.ErrorResponse;
import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.dto.GameRequest;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.game.State;
import com.albiosz.honeycombs.game.dto.GameResponse;
import com.albiosz.honeycombs.turn.Turn;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.util.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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

import static com.albiosz.honeycombs.util.Auth.createURLWithPort;
import static com.albiosz.honeycombs.util.Auth.sendLoginRequest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = HoneycombsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GameControllerTests {

	@LocalServerPort
	private int port;

	private TestRestTemplate restTemplate;
	private HttpHeaders headers;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private String jwtToken;

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:16-alpine"
	)
			.withDatabaseName("honeycombs")
			.withExposedPorts(5432)
			.withUsername("user")
			.withPassword("passwd");


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
		gameRepository.deleteAll();
		userRepository.deleteAll();
		user = userRepository.save(new User("email@email.com", new BCryptPasswordEncoder().encode("password"), "user", true));

		String url = createURLWithPort(port, "/api/auth/login");
		UserLoginDto userLoginDto = new UserLoginDto(user.getUsername(), "password");
		jwtToken = sendLoginRequest(url, userLoginDto, LoginResponse.class).getBody().getToken();

		restTemplate = new TestRestTemplate();
		headers = new HttpHeaders();
	}

	@Test
	@DisplayName("GET /api/game/{id} - Game does not exist")
	void testGetGameById_GameNotFound() {
		ResponseEntity<ErrorResponse> response = sendGetGamesRequest(1, ErrorResponse.class);

		assertEquals(404, response.getStatusCode().value());
		assertTrue(response.getBody().message().contains("Game not found"));
	}

	@Test
	@DisplayName("GET /api/game/{id} - Game exists")
	void testGetGameById() {
		Game game = gameRepository.save(new Game());

		ResponseEntity<String> response = sendGetGamesRequest(game.getId(), String.class);

		assertTrue(Json.isValid(response.getBody(), GameResponse.class));

		GameResponse responseGame = Json.unmarshal(response.getBody(), GameResponse.class, objectMapper);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(game.getId(), responseGame.getId());
	}

	private <T> ResponseEntity<T> sendGetGamesRequest(long gameId, Class<T> expectedReturnType) {
		String url = createURLWithPort(port, "/api/game/" + gameId);
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(org.springframework.http.MediaType.APPLICATION_JSON));

		HttpEntity<Void> entity = new HttpEntity<>(null, headers);

		return restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				expectedReturnType
		);
	}

	@Test
	@DisplayName("GET /api/game - Get games with State=CREATED")
	void testGetGamesByState() {
		List<State> states = List.of(State.CREATED, State.IN_PROGRESS, State.FINISHED, State.FINISHED);
		for (State state : states) {
			Game game = new Game();
			game.setState(state);
			gameRepository.save(game);
		}
		
		String url = createURLWithPort(port, "/api/game?state=CREATED");
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(org.springframework.http.MediaType.APPLICATION_JSON));

		HttpEntity<Void> entity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				String.class
		);

		assertTrue(Json.isValid(response.getBody(), Game[].class));
		Game[] responseGames = Json.unmarshal(response.getBody(), Game[].class, objectMapper);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(1, responseGames.length);
	}

	@Test
	@DisplayName("POST /api/game - Create game")
	void testCreateGame() {
		String url = createURLWithPort(port, "/api/game");
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(org.springframework.http.MediaType.APPLICATION_JSON));

		GameRequest gameRequest = new GameRequest("Game");

		HttpEntity<GameRequest> entity = new HttpEntity<>(gameRequest, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.POST,
				entity,
				String.class
		);


		assertTrue(Json.isValid(response.getBody(), Game.class));
		Game responseGame = Json.unmarshal(response.getBody(), Game.class, objectMapper);

		assertEquals(201, response.getStatusCode().value());
		assertNotNull(responseGame);
		assertEquals("Game", responseGame.getName());
		assertTrue(responseGame.getUserGame(user.getId()).isPresent());
		assertTrue(responseGame.getUserGame(user.getId()).get().isUserHost());
	}

	@Test
	@DisplayName("DELETE /api/game - Game cannot be deleted, it already started")
	void testDeleteGameById_GameAlreadyStarted() {
		Game game = gameRepository.save(new Game("Game to delete"));
		game.setState(State.IN_PROGRESS);
		gameRepository.save(game);

		ResponseEntity<ErrorResponse> response = sendDeleteGameRequest(game.getId(), ErrorResponse.class);

		assertEquals(400, response.getStatusCode().value());
		assertTrue(response.getBody().message().contains("cannot be modified"));
		assertTrue(gameRepository.existsById(game.getId()));
	}

	@Test
	@DisplayName("DELETE /api/game - Game cannot be deleted, user is not in the game")
	void testDeleteGameById_UserNotPart() {
		Game game = gameRepository.save(new Game("Game to delete"));

		ResponseEntity<ErrorResponse> response = sendDeleteGameRequest(game.getId(), ErrorResponse.class);

		assertEquals(400, response.getStatusCode().value());
		assertTrue(response.getBody().message().contains("not in the game"));
		assertTrue(gameRepository.existsById(game.getId()));
	}

	@Test
	@DisplayName("DELETE /api/game - User in the game but is not a host")
	void testDeleteGameById_UserNotHost() {
		Game game = gameRepository.save(new Game("Game to delete"));

		User persistentUser = userRepository.findById(this.user.getId()).orElseThrow();
		persistentUser.joinGame(game, false);
		userRepository.save(persistentUser);

		ResponseEntity<ErrorResponse> response = sendDeleteGameRequest(game.getId(), ErrorResponse.class);

		assertEquals(400, response.getStatusCode().value());
		assertTrue(response.getBody().message().contains("not host of the game"));
		assertTrue(gameRepository.existsById(game.getId()));
	}

	@Test
	@DisplayName("DELETE /api/game - User in the game and is host = game can be deleted")
	void testDeleteGameById() {
		String jdbcUrl = postgres.getJdbcUrl();

		Game game = gameRepository.save(new Game("Game to delete"));

		User persistentUser = userRepository.findById(this.user.getId()).orElseThrow();
		persistentUser.joinGame(game, true);
		persistentUser.getUserGames().getFirst().addTurn(new Turn(1));
		userRepository.save(persistentUser);

		ResponseEntity<Void> response = sendDeleteGameRequest(game.getId(), Void.class);

		assertEquals(200, response.getStatusCode().value());
		assertFalse(gameRepository.existsById(game.getId()));
	}

	private <T> ResponseEntity<T> sendDeleteGameRequest(long gameId, Class<T> expectedReturnType) {
		String url = createURLWithPort(port, "/api/game/" + gameId);
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(org.springframework.http.MediaType.APPLICATION_JSON));

		HttpEntity<T> entity = new HttpEntity<>(null, headers);

		return restTemplate.exchange(
				url,
				HttpMethod.DELETE,
				entity,
				expectedReturnType
		);
	}

	@Test
	@DisplayName("POST /api/game/{$id}/user - Game not found")
	void testAddUser_GameNotFound() {
		ResponseEntity<ErrorResponse> response = sendAddUserRequest(1, ErrorResponse.class);

		assertEquals(404, response.getStatusCode().value());
		assertTrue(response.getBody().message().contains("Game not found"));
	}

	@Test
	@DisplayName("POST /api/game/{$id}/user - Game already started, user cannot join the game")
	void testAddUser_GameAlreadyStarted() {
		Game game = gameRepository.save(new Game());
		game.setState(State.IN_PROGRESS);
		gameRepository.save(game);

		ResponseEntity<ErrorResponse> response = sendAddUserRequest(game.getId(), ErrorResponse.class);

		assertEquals(400, response.getStatusCode().value());
		assertTrue(response.getBody().message().contains("cannot be modified"));

		game = gameRepository.findById(game.getId()).orElseThrow();
		assertEquals(0, game.getUserGames().size());
	}

	@Test
	@DisplayName("POST /api/game/{$id}/user - User joins the game successfully")
	void testAddUser() {
		Game game = gameRepository.save(new Game());

		ResponseEntity<String> response = sendAddUserRequest(game.getId(), String.class);
		assertEquals(200, response.getStatusCode().value());

		assertTrue(Json.isValid(response.getBody(), GameResponse.class));

		GameResponse responseGame = Json.unmarshal(response.getBody(), GameResponse.class, objectMapper);

		assertEquals(1, responseGame.getUserGames().size());
		assertNotNull(responseGame.getUserGames().get(user.getId()).getUser());

		game = gameRepository.findById(game.getId()).orElseThrow();
		assertEquals(1, game.getUserGames().size());
	}

	private <T> ResponseEntity<T> sendAddUserRequest(long gameId, Class<T> expectedReturnType) {
		String url = createURLWithPort(port, String.format("/api/game/%d/user", gameId));
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(org.springframework.http.MediaType.APPLICATION_JSON));

		HttpEntity<Void> entity = new HttpEntity<>(null, headers);

		return restTemplate.exchange(
				url,
				HttpMethod.POST,
				entity,
				expectedReturnType
		);
	}
}
