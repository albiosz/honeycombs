package com.albiosz.honeycombs.game;

import org.springframework.stereotype.Service;

@Service
public class GameService {

	private final GameRepository gameRepository;

	public GameService(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
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
}
