package com.albiosz.honeycombs.auth;

import com.albiosz.honeycombs.auth.dto.UserLoginRequest;
import com.albiosz.honeycombs.auth.dto.UserRegisterRequest;
import com.albiosz.honeycombs.auth.dto.UserVerifyRequest;
import com.albiosz.honeycombs.auth.exceptions.*;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	public AuthService(UserRepository userRepository, EmailService emailService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}

	public User login(UserLoginRequest input) throws AuthenticationException {
		User user = userRepository.findByUsername(input.getUsername())
				.orElseThrow(InvalidLoginData::new);

		if (!user.isEnabled()) {
			throw new LoginNotPossible();
		}

		try {
			// authenticationManager.authenticate() the user with the given username and password
			// authenticationProvider (ApplicationConfiguration) sets that the authentication is done by checking the username and encrypted password
			// TODO: But how authenticationManager gets access to the authenticationProvider()?
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							input.getUsername(),
							input.getPassword()
					)
			);
		} catch (AuthenticationException e) {
			throw new InvalidLoginData();
		}
		return user;
	}

	public User register(UserRegisterRequest input) {
		User user = new User(input.getUsername(), passwordEncoder.encode(input.getPassword()), input.getNickname(), false);
		user.setVerificationCode(generateVerificationCode());
		user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(60L * 15));
		try {
			userRepository.save(user);
		} catch (Exception e) {
			throw new RegistrationFailed();
		}

		emailService.sendVerificationEmail(user);
		return user;
	}

	public void verify(UserVerifyRequest input) {
		User user = userRepository.findByUsername(input.getUsername())
				.orElseThrow(VerificationFailed::new);

		if (user.isVerificationCodeExpired()) {
			throw new VerificationFailed();
		}
		if (!user.getVerificationCode().equals(input.getVerificationCode())) {
			throw new VerificationFailed();
		}
		user.setEnabled(true);
		user.setVerificationCode(null);
		user.setVerificationCodeExpiresAt(null);
		userRepository.save(user);
	}

	public void resendVerificationCode(String email) {
		User user = userRepository.findByUsername(email)
				.orElseThrow(ResendingVerificationCodeFailed::new);

		if (user.isEnabled()) {
			throw new ResendingVerificationCodeFailed();
		}
		user.setVerificationCode(generateVerificationCode());
		user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(60L * 15));
		emailService.sendVerificationEmail(user);
		userRepository.save(user);
	}

	private String generateVerificationCode() {
		Random random = new Random();
		int code = random.nextInt(900000) + 100000;
		return String.valueOf(code);
	}
}
