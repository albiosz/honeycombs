package com.albiosz.honeycombs.e2e.user;

import com.albiosz.honeycombs.HoneycombsApplication;
import com.albiosz.honeycombs.auth.dto.UserLoginDto;
import com.albiosz.honeycombs.auth.response.LoginResponse;
import com.albiosz.honeycombs.config.exceptions.ErrorResponse;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.user.dto.UserResponse;
import com.albiosz.honeycombs.util.JsonSchema;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.UUID;

import static com.albiosz.honeycombs.util.Auth.createURLWithPort;
import static com.albiosz.honeycombs.util.Auth.sendLoginRequest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = HoneycombsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserControllerTests {
	@LocalServerPort
	private int port;

	private TestRestTemplate restTemplate;
	private HttpHeaders headers;

	@Autowired
	private UserRepository userRepository;

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
		userRepository.deleteAll();
		user = userRepository.save(new User("email@email.com", new BCryptPasswordEncoder().encode("password"), "user", true));

		String url = createURLWithPort(port, "/api/auth/login");
		UserLoginDto userLoginDto = new UserLoginDto(user.getUsername(), "password");
		jwtToken = sendLoginRequest(url, userLoginDto, LoginResponse.class).getBody().getToken();

		restTemplate = new TestRestTemplate();
		headers = new HttpHeaders();
	}

	@Test
	@DisplayName("GET /api/user/{userId} - not found")
	void getUserByIdNotFound() {
		ResponseEntity<ErrorResponse> response = sendGetUserByIdRequest(UUID.randomUUID(), ErrorResponse.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertTrue(response.getBody().message().contains("User not found"));
	}

	@Test
	@DisplayName("GET /api/user/{userId} - success")
	void getUserById() {
		ResponseEntity<String> response = sendGetUserByIdRequest(user.getId(), String.class);

		String jsonSchema = assertDoesNotThrow(() -> JsonSchema.generateJsonSchema(UserResponse.class));
		boolean isValid = assertDoesNotThrow(() -> JsonSchema.isValidJson(response.getBody(), jsonSchema));
		assertTrue(isValid);
	}

	<T> ResponseEntity<T> sendGetUserByIdRequest(UUID userId, Class<T> responseType) {
		String url = createURLWithPort(port, "/api/user/" + userId);
		headers.setBearerAuth(jwtToken);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<T> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				responseType
		);
	}
}
