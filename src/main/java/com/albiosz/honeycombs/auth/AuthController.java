package com.albiosz.honeycombs.auth;

import com.albiosz.honeycombs.auth.dto.UserLoginDto;
import com.albiosz.honeycombs.auth.dto.UserRegisterDto;
import com.albiosz.honeycombs.auth.dto.UserResendVerificationDto;
import com.albiosz.honeycombs.auth.dto.UserVerifyDto;
import com.albiosz.honeycombs.auth.response.LoginResponse;
import com.albiosz.honeycombs.user.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody UserLoginDto userLoginDto) {
		User user = authService.login(userLoginDto);
		String jwtToken = jwtService.generateToken(user);
		LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
		return ResponseEntity.ok(loginResponse);
	}

	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody UserRegisterDto userRegisterDto) {
		authService.register(userRegisterDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify")
	public ResponseEntity<Void> verify(@RequestBody UserVerifyDto userVerifyDto) {
		authService.verify(userVerifyDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<Void> resendVerification(@RequestBody UserResendVerificationDto userResendVerificationDto) {
		authService.resendVerificationCode(userResendVerificationDto.getUsername());
		return ResponseEntity.ok().build();
	}
}
