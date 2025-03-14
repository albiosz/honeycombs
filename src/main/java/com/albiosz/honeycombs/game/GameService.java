package com.albiosz.honeycombs.game;

import com.albiosz.honeycombs.game.exceptions.GameNotModifiable;
import com.albiosz.honeycombs.game.exceptions.GameNotFound;
import com.albiosz.honeycombs.game.exceptions.UserNotGameHost;
import com.albiosz.honeycombs.game.exceptions.UserNotInGame;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.user.exceptions.UserNotFound;
import com.albiosz.honeycombs.usergame.UserGame;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

	private final GameRepository gameRepository;
	private final UserRepository userRepository;

	public GameService(GameRepository gameRepository, UserRepository userRepository) {
		this.gameRepository = gameRepository;
		this.userRepository = userRepository;
	}

	public Game createGame(String name) {
		Game game = gameRepository.save(new Game(name));
		addCurrentUserToGame(game, true);
		return game;
	}

	public Game getGameById(long id) {
		return gameRepository.findById(id)
				.orElseThrow(GameNotFound::new);
	}

	public List<Game> getGamesByState(State state) {
		return gameRepository.findByState(state);
	}

	public void deleteGameById(long id) {
		Game gameToDelete = gameRepository.findById(id)
				.orElseThrow(GameNotFound::new);

		if (!gameToDelete.getState().equals(State.CREATED)) {
			throw new GameNotModifiable();
		}

		User userFromContext = getUserFromContext();
		UserGame userGame = gameToDelete.getUserGame(userFromContext.getId())
				.orElseThrow(UserNotInGame::new);

		if (!userGame.isUserHost()) {
			throw new UserNotGameHost();
		}

		gameRepository.deleteById(id);
	}

	public Game addUser(long gameId) {
		Game game = gameRepository.findById(gameId)
				.orElseThrow(GameNotFound::new);

		if (!game.getState().equals(State.CREATED)) {
			throw new GameNotModifiable();
		}

		addCurrentUserToGame(game, false);
		return game;
	}

	private void addCurrentUserToGame(Game game, boolean isUserHost) {
		User userFromContext = getUserFromContext();

		User user = userRepository.findById(userFromContext.getId())
				.orElseThrow(UserNotFound::new);
		user.leaveOtherLobbies(game);
		user.joinGame(game, isUserHost);

		userRepository.flush();
	}

	private User getUserFromContext() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
