package com.albiosz.honeycombs.integration;

import com.albiosz.honeycombs.HoneycombsApplication;
import com.albiosz.honeycombs.auth.dto.UserLoginDto;
import com.albiosz.honeycombs.auth.response.LoginResponse;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.albiosz.honeycombs.integration.util.Auth.createURLWithPort;
import static com.albiosz.honeycombs.integration.util.Auth.sendLoginRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = HoneycombsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AuthTests {
	@LocalServerPort
	private int port;

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

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		userRepository.save(new User("email@email.com", new BCryptPasswordEncoder().encode("password"), "user", true));
	}

	@Test
	void testLogin() {
		String url = createURLWithPort(port, "/auth/login");
		UserLoginDto userLoginDto = new UserLoginDto("email@email.com", "password");
		ResponseEntity<LoginResponse> response = sendLoginRequest(url, userLoginDto);
		assertEquals(200, response.getStatusCode().value());
	}

}
