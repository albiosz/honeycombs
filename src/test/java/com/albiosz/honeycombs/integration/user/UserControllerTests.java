package com.albiosz.honeycombs.integration.user;

import com.albiosz.honeycombs.auth.JwtService;
import com.albiosz.honeycombs.integration.util.AllRepositoryBeans;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.UUID;

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

	@BeforeEach
	void setUp() {
		jwtToken = jwtService.generateToken(new User("email@email.com", "password", "user", true));
		when(userRepository.findByUsername("email@email.com")).thenReturn(Optional.of(new User("email@email.com", "password", "user", true)));
	}

	@Test
	void testGetUserById() throws Exception {
		UUID uuid = UUID.randomUUID();
		when(userRepository.findById(uuid)).thenReturn(Optional.of(new User("email", "password", "user", true)));

		RequestBuilder request = get("/api/user/{uuid}", uuid)
				.header("Authorization", "Bearer " + jwtToken);

		mockMvc.perform(request)
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
