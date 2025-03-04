package com.albiosz.honeycombs.game;


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
	public List<Game> getGamesByState(State state) {
		return gameService.getGamesByState(state);
	}

	@PostMapping
	public ResponseEntity<Game> createGame(@RequestBody GameDto gameDto) {
		Game createdGame = gameService.createGame(gameDto.getName());
		return ResponseEntity.status(201).body(createdGame);
	}

	@DeleteMapping("/{id}")
	public void deleteGameById(@PathVariable long id) {
		gameService.deleteGameById(id);
	}

	@PutMapping("/{id}/add-user")
	public ResponseEntity<Game> addUser(@PathVariable long id) {
		Game game = gameService.addUser(id);
		return ResponseEntity.ok(game);
	}
}
