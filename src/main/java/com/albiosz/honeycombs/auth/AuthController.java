package com.albiosz.honeycombs.auth;

import com.albiosz.honeycombs.auth.dto.UserLoginRequest;
import com.albiosz.honeycombs.auth.dto.UserRegisterRequest;
import com.albiosz.honeycombs.auth.dto.UserResendVerificationRequest;
import com.albiosz.honeycombs.auth.dto.UserVerifyRequest;
import com.albiosz.honeycombs.auth.response.LoginResponse;
import com.albiosz.honeycombs.user.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
		User user = authService.login(userLoginRequest);
		String jwtToken = jwtService.generateToken(user);
		LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
		return ResponseEntity.ok(loginResponse);
	}

	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody UserRegisterRequest userRegisterRequest) {
		authService.register(userRegisterRequest);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify")
	public ResponseEntity<Void> verify(@RequestBody UserVerifyRequest userVerifyRequest) {
		authService.verify(userVerifyRequest);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<Void> resendVerification(@RequestBody UserResendVerificationRequest userResendVerificationRequest) {
		authService.resendVerificationCode(userResendVerificationRequest.getUsername());
		return ResponseEntity.ok().build();
	}
}
