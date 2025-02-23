package com.albiosz.honeycombs.integration.user;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.turn.Turn;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.usergame.UserGame;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTests {

	private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:16-alpine"
	)
			.withExposedPorts(5432) // this is the port that the db has inside the container; it is exposed to my maschine on a random port
			.withDatabaseName("postgresql")
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
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();

		User user = userRepository.save(new User("email@email.com", "password", "user", true));
		Game game = gameRepository.save(new Game());

		UserGame userGame = user.joinGame(game, true);

		userGame.addTurn(new Turn(10));
		userGame.addTurn(new Turn(8));
	}

	@Test
	void testFindByUsername() {
		User user = userRepository.findByUsername("email@email.com").orElseThrow();

		assertEquals("email@email.com", user.getUsername());

		UserGame userGame = user.getUserGames().getFirst();
		assertNotNull(userGame.getGame());
	}

	@Test
	void testJoinGame() {
		User user = new User("second@user.com", "password", "second-user", true);
		user = userRepository.save(user);

		assertTrue(user.getUserGames().isEmpty());

		Game game = gameRepository.findAll().getFirst();
		user.joinGame(game, true);

		assertNotNull(user.getUserGames().getFirst());

		user = userRepository.findByUsername("second@user.com").orElseThrow();
		assertNotNull(user.getUserGames().getFirst());
	}
}
