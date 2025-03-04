package com.albiosz.honeycombs.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

	@Schema(description = "Username (username in email format)", example = "email@email.com")
	private String username;

	@Schema(description = "Password", example = "password")
	private String password;
}
