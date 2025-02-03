package com.albiosz.honeycombs.auth;

import com.albiosz.honeycombs.auth.dto.UserLoginDto;
import com.albiosz.honeycombs.auth.dto.UserRegisterDto;
import com.albiosz.honeycombs.auth.dto.UserResendVerificationDto;
import com.albiosz.honeycombs.auth.dto.UserVerifyDto;
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
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
		User user;
		try {
			user = authService.login(userLoginDto);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		String jwtToken = jwtService.generateToken(user);
		LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
		return ResponseEntity.ok(loginResponse);
	}

	// TODO: Is the user correct as a response value? The client will also receive a passowrd (bcrypted) of the user.
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody UserRegisterDto userRegisterDto) {
		try {
			User user = authService.register(userRegisterDto);
			return ResponseEntity.ok("User registered successfully! Verification code sent to your email: " + user.getUsername());
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/verify")
	public ResponseEntity<String> verify(@RequestBody UserVerifyDto userVerifyDto) {
		try {
			authService.verify(userVerifyDto);
			return ResponseEntity.ok("Account verified successfully!");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<String> resendVerification(@RequestBody UserResendVerificationDto userResendVerificationDto) {
		try {
			authService.resendVerificationCode(userResendVerificationDto.getUsername());
			return ResponseEntity.ok("Verification code sent successfully!");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
