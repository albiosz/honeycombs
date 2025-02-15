package com.albiosz.honeycombs.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<User> getUserById(@PathVariable UUID uuid) {
		User user = userService.getUserById(uuid);
		return ResponseEntity.ok(user);
	}
}
