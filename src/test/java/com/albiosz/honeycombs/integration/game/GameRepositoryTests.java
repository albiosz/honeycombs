package com.albiosz.honeycombs.integration.game;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.game.State;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GameRepositoryTests {

	private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:16-alpine"
	)
			.withExposedPorts(5432) // this is the port that the db has inside the container; it is exposed to my maschine on a random port
			.withDatabaseName("honeycombs")
			.withPassword("passwd")
			.withUsername("user");

	@DynamicPropertySource
	static void configureDatasource(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		List<State> states = List.of(State.CREATED, State.CREATED, State.IN_PROGRESS, State.IN_PROGRESS, State.FINISHED);

		for (State state : states) {
			Game game = new Game();
			game.setState(state);
			gameRepository.save(game);
		}
		gameRepository.flush();
	}

	@Test
	void testFindByState() {
		List<Game> createdGames = gameRepository.findByState(State.CREATED);
		List<Game> inProgressGames = gameRepository.findByState(State.IN_PROGRESS);
		List<Game> finishedGames = gameRepository.findByState(State.FINISHED);

		assertEquals(2, createdGames.size());
		assertEquals(2, inProgressGames.size());
		assertEquals(1, finishedGames.size());
	}

	@Test
	void testFindByIdWithUserGame() {
//		String jdbcUrl = postgres.getJdbcUrl();

		Game game = gameRepository.save(new Game());
		gameRepository.flush();

		User user = userRepository.save(new User("email", "password", "username", true));
		user.joinGame(game, true);
		userRepository.flush();

		assertEquals(1, game.getUserGames().size());
	}
}
