package com.albiosz.honeycombs.game;

import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GameService {

	private final GameRepository gameRepository;
	private final UserRepository userRepository;

	public GameService(GameRepository gameRepository, UserRepository userRepository) {
		this.gameRepository = gameRepository;
		this.userRepository = userRepository;
	}

	public Game createGame() {
		Game game = new Game();
		return gameRepository.save(game);
	}

	public Game getGameById(long id) {
		return gameRepository.findById(id).orElseThrow();
	}

	public void deleteGameById(long id) {
		gameRepository.deleteById(id);
	}

	public Game addUser(long gameId) {
		Game game = gameRepository.findById(gameId)
				.orElseThrow(() -> new RuntimeException("Game not found"));

		if (!game.getState().equals(State.CREATED)) {
			throw new RuntimeException("Game is already started");
		}

		User userFromContext = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		User user = userRepository.findById(userFromContext.getId()).orElseThrow();
		user.leaveOtherLobbies(game);
		user.joinGame(game);

		userRepository.flush();
		return game;
	}
}
