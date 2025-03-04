package com.albiosz.honeycombs.integration.user;

import com.albiosz.honeycombs.auth.JwtService;
import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.user.dto.UserResponse;
import com.albiosz.honeycombs.util.AllRepositoryBeans;
import com.albiosz.honeycombs.util.JsonSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(
		properties = {
				"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
		}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AllRepositoryBeans.class)
class UserControllerTests {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	private String jwtToken;

	private User user;

	@BeforeEach
	void setUp() {
		jwtToken = jwtService.generateToken(new User("email@email.com", "password", "user", true));
		user = new User("email@email.com", "password", "user", true);
		user.setId(UUID.randomUUID());

		Game game = new Game();
		game.setId(1L);
		user.joinGame(game, true);
		when(userRepository.findByUsername("email@email.com")).thenReturn(Optional.of(user));
	}

	@Test
	void testGetUserById() {
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		RequestBuilder request = get("/api/user/{uuid}", user.getId())
				.header("Authorization", "Bearer " + jwtToken);

		MvcResult mvcResult = assertDoesNotThrow(() -> mockMvc.perform(request)
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId().toString()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
					.andExpect(MockMvcResultMatchers.jsonPath("$.isEnabled").value(user.isEnabled()))
					.andReturn()
		);

		String body = assertDoesNotThrow(() -> mvcResult.getResponse().getContentAsString());

		String jsonSchema = assertDoesNotThrow(() -> JsonSchema.generateJsonSchema(UserResponse.class));
		boolean isValid = assertDoesNotThrow(() ->JsonSchema.isValidJson(body, jsonSchema));
		assertTrue(isValid);
	}
}
