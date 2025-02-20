package com.albiosz.honeycombs.game;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/game")
public class GameController {

	private final GameService gameService;

	public GameController(GameService gameService) {
		this.gameService = gameService;
	}

	@PostMapping
	public ResponseEntity<Game> createGame() {
		Game createdGame = gameService.createGame();
		return ResponseEntity.status(201).body(createdGame);
	}

	@GetMapping
	public Game getGameById(long id) {
		return gameService.getGameById(id);
	}

	@DeleteMapping
	public void deleteGameById(long id) {
		gameService.deleteGameById(id);
	}

	@PutMapping("/add-user/{gameId}")
	public ResponseEntity<Game> addUser(@PathVariable long gameId) {
		Game game = gameService.addUser(gameId);
		return ResponseEntity.ok(game);
	}
}
