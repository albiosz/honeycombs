package com.albiosz.honeycombs.integration;

import com.albiosz.honeycombs.HoneycombsApplication;
import com.albiosz.honeycombs.auth.dto.UserLoginDto;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;


import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = HoneycombsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Auth {
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
		userRepository.save(new User("user", new BCryptPasswordEncoder().encode("password"), "email@email.com", true));
	}

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();

	@Test
	void testLogin() {
		UserLoginDto credentials = new UserLoginDto("user", "password");

		HttpEntity<UserLoginDto> entity = new HttpEntity<>(credentials, headers);

		ResponseEntity<String> response = restTemplate.exchange(
			createURLWithPort("/auth/login"),
			HttpMethod.POST, entity, String.class
		);

		int responseCode = response.getStatusCode().value();

		assertEquals(200, responseCode);
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}
