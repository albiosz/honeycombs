package com.albiosz.honeycombs.user;

import com.albiosz.honeycombs.user.exceptions.UserNotFound;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

		private final UserRepository userRepository;

		public UserService(UserRepository userRepository) {
			this.userRepository = userRepository;
		}

		public User getUserById(UUID uuid) {
			return userRepository.findById(uuid)
					.orElseThrow(UserNotFound::new);
		}
}
