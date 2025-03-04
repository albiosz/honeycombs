package com.albiosz.honeycombs.integration.game;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.game.State;
import com.albiosz.honeycombs.game.dto.GameResponse;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.util.JsonSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.albiosz.honeycombs.util.Auth.createURLWithPort;
import static com.albiosz.honeycombs.util.Auth.loginAndGetToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
		}
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GameControllerTests {
	@LocalServerPort
	private int port;

	@MockitoBean
	private final GameRepository gameRepository = mock(GameRepository.class);

	@MockitoBean
	private final UserRepository userRepository = mock(UserRepository.class);

	private String jwtToken;

	@BeforeEach
	void setUp() {
		jwtToken = loginAndGetToken(port, userRepository);

		Game game = new Game("new game");
		game.setId(1L);

		User user = new User("email", "password", "nickname", true);
		user.setId(UUID.randomUUID());
		user.joinGame(game, true);

		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
	}

	@Test
	void testGetGame() {

		String url = createURLWithPort(port, "/api/game/1");

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = new TestRestTemplate()
				.exchange(url, HttpMethod.GET, entity, String.class);

		assertEquals(200, response.getStatusCode().value());

		String jsonSchema = assertDoesNotThrow(() -> JsonSchema.generateJsonSchema(GameResponse.class));
		boolean isValid = assertDoesNotThrow(() -> JsonSchema.isValidJson(response.getBody(), jsonSchema));
		assertTrue(isValid);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		GameResponse gameResponse = assertDoesNotThrow(() -> mapper.readValue(response.getBody(), GameResponse.class));
		assertEquals(State.CREATED, gameResponse.getState());
	}
}
