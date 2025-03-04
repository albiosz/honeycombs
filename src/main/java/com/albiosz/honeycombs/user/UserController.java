package com.albiosz.honeycombs.user;

import com.albiosz.honeycombs.user.dto.UserResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

	private final ModelMapper modelMapper;

	private final UserService userService;

	public UserController(UserService userService, ModelMapper modelMapper) {
		this.userService = userService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable UUID uuid) {
		User user = userService.getUserById(uuid);
		UserResponse userResponse = UserResponse.fromUser(user, modelMapper);
		return ResponseEntity.ok(userResponse);
	}
}
