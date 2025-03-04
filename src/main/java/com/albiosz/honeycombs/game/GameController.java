package com.albiosz.honeycombs.game;


import com.albiosz.honeycombs.game.dto.GameRequest;
import com.albiosz.honeycombs.game.dto.GameResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/game")
public class GameController {

	private final ModelMapper modelMapper;

	private final GameService gameService;

	public GameController(GameService gameService, ModelMapper modelMapper) {
		this.gameService = gameService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/{id}")
	public ResponseEntity<GameResponse> getGameById(@PathVariable long id) {
		Game game = gameService.getGameById(id);
		GameResponse gameResponse = GameResponse.fromGame(game, modelMapper);
		return ResponseEntity.ok(gameResponse);

	}

	@GetMapping
	public ResponseEntity<List<GameResponse>> getGamesByState(State state) {
		List<Game> games = gameService.getGamesByState(state);
		List<GameResponse> gameResponses = games.stream()
				.map(game -> GameResponse.fromGame(game, modelMapper))
				.toList();
		return ResponseEntity.ok(gameResponses);
	}

	@PostMapping
	public ResponseEntity<GameResponse> createGame(@RequestBody GameRequest gameRequest) {
		Game createdGame = gameService.createGame(gameRequest.getName());
		GameResponse gameResponse = GameResponse.fromGame(createdGame, modelMapper);
		return ResponseEntity.status(201).body(gameResponse);
	}

	@DeleteMapping("/{id}")
	public void deleteGameById(@PathVariable long id) {
		gameService.deleteGameById(id);
	}

	@PostMapping("/{id}/user")
	public ResponseEntity<GameResponse> addUser(@PathVariable long id) {
		Game game = gameService.addUser(id);
		GameResponse gameResponse = GameResponse.fromGame(game, modelMapper);
		return ResponseEntity.ok(gameResponse);
	}
}
