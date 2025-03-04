package com.albiosz.honeycombs.util;

import com.albiosz.honeycombs.auth.dto.UserLoginRequest;
import com.albiosz.honeycombs.auth.response.LoginResponse;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class Auth {

	public static String loginAndGetToken(int port, UserRepository userRepository) {
		when(userRepository.findByUsername("email@email.com")).thenReturn(Optional.of(new User("email@email.com", new BCryptPasswordEncoder().encode("password"), "user", true)));

		String url = createURLWithPort(port, "/api/auth/login");
		UserLoginRequest credentials = new UserLoginRequest("email@email.com", "password");

		ResponseEntity<LoginResponse> response = sendLoginRequest(url, credentials, LoginResponse.class);

		assert(response.getBody() != null);

		return response.getBody().getToken();

	}

	public static <T> ResponseEntity<T> sendLoginRequest(String url, UserLoginRequest credentials, Class<T> responseType) {
		TestRestTemplate restTemplate = new TestRestTemplate();
		HttpHeaders headers = new HttpHeaders();

		HttpEntity<UserLoginRequest> entity = new HttpEntity<>(credentials, headers);

		return restTemplate.exchange(
				url,
				HttpMethod.POST,
				entity,
				responseType
		);
	}

	public static String createURLWithPort(int port, String uri) {
		return "http://localhost:" + port + uri;
	}
}
