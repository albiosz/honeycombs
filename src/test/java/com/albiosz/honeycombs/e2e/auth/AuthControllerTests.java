package com.albiosz.honeycombs.e2e.auth;

import com.albiosz.honeycombs.HoneycombsApplication;
import com.albiosz.honeycombs.auth.dto.UserLoginRequest;
import com.albiosz.honeycombs.auth.dto.UserRegisterRequest;
import com.albiosz.honeycombs.auth.dto.UserResendVerificationRequest;
import com.albiosz.honeycombs.auth.dto.UserVerifyRequest;
import com.albiosz.honeycombs.auth.response.LoginResponse;
import com.albiosz.honeycombs.config.exceptions.ErrorResponse;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Instant;

import static com.albiosz.honeycombs.util.Auth.createURLWithPort;
import static com.albiosz.honeycombs.util.Auth.sendLoginRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = HoneycombsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AuthControllerTests {
	@LocalServerPort
	private int port;

	private TestRestTemplate restTemplate;
	private HttpHeaders headers;

	@MockitoBean
	private JavaMailSender javaMailSender;

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

	private User user;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		user = userRepository.save(new User("email@email.com", new BCryptPasswordEncoder().encode("password"), "user", true));
		when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

		restTemplate = new TestRestTemplate();
		headers = new HttpHeaders();
	}

	@Test
	@DisplayName("POST /api/auth/login - invalid credentials")
	void testLogin_userNotFound() {
		String url = createURLWithPort(port, "/api/auth/login");
		UserLoginRequest userLoginDto = new UserLoginRequest("not@existent.com", "pass");
		ResponseEntity<ErrorResponse> response = sendLoginRequest(url, userLoginDto, ErrorResponse.class);
		assertEquals(401, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/login - user not enabled")
	void testLogin_userNotEnabled() {
		User createdUser = userRepository.save(new User("new@email.com", new BCryptPasswordEncoder().encode("password"), "new_user", false));
		String url = createURLWithPort(port, "/api/auth/login");
		UserLoginRequest userLoginDto = new UserLoginRequest(createdUser.getUsername(), "password");
		ResponseEntity<ErrorResponse> response = sendLoginRequest(url, userLoginDto, ErrorResponse.class);
		assertEquals(403, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/login - invalid password")
	void testLogin_invalidPassword() {
		String url = createURLWithPort(port, "/api/auth/login");
		UserLoginRequest userLoginDto = new UserLoginRequest(user.getUsername(), "invalid_password");
		ResponseEntity<ErrorResponse> response = sendLoginRequest(url, userLoginDto, ErrorResponse.class);
		assertEquals(401, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/login - success")
	void testLogin() {
		String url = createURLWithPort(port, "/api/auth/login");
		UserLoginRequest userLoginRequest = new UserLoginRequest("email@email.com", "password");
		ResponseEntity<LoginResponse> response = sendLoginRequest(url, userLoginRequest, LoginResponse.class);
		assertEquals(200, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/register - email/username already exists")
	void testRegister_emailExists() {
		UserRegisterRequest userRegisterDto = new UserRegisterRequest(user.getUsername(), "password", "new_user");
		var response = sendRegisterRequest(userRegisterDto, ErrorResponse.class);

		verify(javaMailSender, Mockito.never()).send(Mockito.any(MimeMessage.class));
		assertEquals(403, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/register - nickname already exists")
	void testRegister_nicknameExists() {
		UserRegisterRequest userRegisterDto = new UserRegisterRequest("new@email.com", "password", user.getNickname());
		var response = sendRegisterRequest(userRegisterDto, ErrorResponse.class);

		verify(javaMailSender, Mockito.never()).send(Mockito.any(MimeMessage.class));
		assertEquals(403, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/register - success")
	void testRegister_success() throws MessagingException {
		UserRegisterRequest userRegisterDto = new UserRegisterRequest("email1@email.com", "password", "user1");

		ResponseEntity<String> response = sendRegisterRequest(userRegisterDto, String.class);

		verifyEmailSubject("Account Verification");

		assertEquals(200, response.getStatusCode().value());
	}

	private <T> ResponseEntity<T> sendRegisterRequest(UserRegisterRequest userRegisterRequest, Class<T> responseType) {
		HttpEntity<UserRegisterRequest> entity = new HttpEntity<>(userRegisterRequest, headers);

		return restTemplate.exchange(
				createURLWithPort(port, "/api/auth/register"),
				HttpMethod.POST,
				entity,
				responseType
		);
	}

	@Test
	@DisplayName("POST /api/auth/verify - no user found")
	void testVerify_userNotFound() {
		UserVerifyRequest userVerifyDto = new UserVerifyRequest("not@found.com", "123456");

		ResponseEntity<ErrorResponse> response = sendVerificationRequest(userVerifyDto, ErrorResponse.class);

		assertEquals(403, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/verify - verification code expired")
	void testVerify_verificationCodeExpired() {
		String verificationCode = "123456";
		String username = "mock@mock.com";
		User notVerifiedUser = saveNotVerifiedUser(username, verificationCode);
		notVerifiedUser.setVerificationCodeExpiresAt(Instant.now().minusSeconds(60L));
		userRepository.save(notVerifiedUser);

		UserVerifyRequest userVerifyDto = new UserVerifyRequest(username, verificationCode);
		var response = sendVerificationRequest(userVerifyDto, ErrorResponse.class);

		assertEquals(403, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/verify - verification code incorrect")
	void testVerify_verificationCodeIncorrect() {
		String verificationCode = "123456";
		String username = "mock@mock.com";
		saveNotVerifiedUser(username, verificationCode);

		UserVerifyRequest userVerifyDto = new UserVerifyRequest(username, "999999");
		var response = sendVerificationRequest(userVerifyDto, ErrorResponse.class);

		assertEquals(403, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/verify - success")
	void testVerify_success() {
		String verificationCode = "123456";
		String username = "mock@mock.com";
		saveNotVerifiedUser(username, verificationCode);

		UserVerifyRequest userVerifyDto = new UserVerifyRequest(username, verificationCode);
		var response = sendVerificationRequest(userVerifyDto, Void.class);

		assertEquals(200, response.getStatusCode().value());
	}

	private <T> ResponseEntity<T> sendVerificationRequest(UserVerifyRequest userVerifyRequest, Class<T> responseType) {
		HttpEntity<UserVerifyRequest> entity = new HttpEntity<>(userVerifyRequest, headers);

		return restTemplate.exchange(
				createURLWithPort(port, "/api/auth/verify"),
				HttpMethod.POST,
				entity,
				responseType
		);
	}

	@Test
	@DisplayName("POST /api/auth/resend-verification - user not found")
	void testResendVerificationCode_userNotFound() {
		UserResendVerificationRequest userResendVerificationDto = new UserResendVerificationRequest("not@existent.com");
		ResponseEntity<ErrorResponse> response = sendResendingVerificationCodeException(userResendVerificationDto, ErrorResponse.class);
		assertEquals(403, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/resend-verification - user already verified")
	void testResendVerificationCode_userAlreadyVerified() {
		UserResendVerificationRequest userResendVerificationDto = new UserResendVerificationRequest(user.getUsername());
		ResponseEntity<ErrorResponse> response = sendResendingVerificationCodeException(userResendVerificationDto, ErrorResponse.class);
		assertEquals(403, response.getStatusCode().value());
	}

	@Test
	@DisplayName("POST /api/auth/resend-verification - successful")
	void testResendVerificationCode_success() throws MessagingException {
		String verificationCode = "123456";
		String username = "mock@mock.com";
		saveNotVerifiedUser(username, verificationCode);

		UserResendVerificationRequest userResendVerificationDto = new UserResendVerificationRequest(username);
		var response = sendResendingVerificationCodeException(userResendVerificationDto, Void.class);

		verifyEmailSubject("Account Verification");
		assertEquals(200, response.getStatusCode().value());
	}

	private <T> ResponseEntity<T> sendResendingVerificationCodeException(UserResendVerificationRequest userResendVerificationRequest, Class<T> responseType) {
		HttpEntity<UserResendVerificationRequest> entity = new HttpEntity<>(userResendVerificationRequest, headers);

		return restTemplate.exchange(
				createURLWithPort(port, "/api/auth/resend-verification"),
				HttpMethod.POST,
				entity,
				responseType
		);
	}

	User saveNotVerifiedUser(String username, String verificationCode) {
		User user = new User(username, new BCryptPasswordEncoder().encode("password"), "mock", false);
		user.setVerificationCode(verificationCode);
		user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(60L * 15));
		return userRepository.save(user);
	}

	void verifyEmailSubject(String emailContent) throws MessagingException {
		ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
		Mockito.verify(javaMailSender).send(messageCaptor.capture());
		assertTrue(messageCaptor.getValue().getSubject().contains(emailContent));
	}
}
