package com.albiosz.honeycombs.game;


import com.albiosz.honeycombs.game.dto.GameRequest;
import com.albiosz.honeycombs.game.dto.GameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<GameResponse> getGameById(@PathVariable long id) {
		Game game = gameService.getGameById(id);
		GameResponse gameResponse = GameResponse.fromGame(game, modelMapper);
		return ResponseEntity.ok(gameResponse);

	}

	@GetMapping
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<GameResponse>> getGamesByState(State state) {
		List<Game> games = gameService.getGamesByState(state);
		List<GameResponse> gameResponses = games.stream()
				.map(game -> GameResponse.fromGame(game, modelMapper))
				.toList();
		return ResponseEntity.ok(gameResponses);
	}

	@PostMapping
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<GameResponse> createGame(@RequestBody GameRequest gameRequest) {
		Game createdGame = gameService.createGame(gameRequest.getName());
		GameResponse gameResponse = GameResponse.fromGame(createdGame, modelMapper);
		return ResponseEntity.status(201).body(gameResponse);
	}

	@DeleteMapping("/{id}")
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public void deleteGameById(@PathVariable long id) {
		gameService.deleteGameById(id);
	}

	@PostMapping("/{id}/user")
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<GameResponse> addUser(@PathVariable long id) {
		Game game = gameService.addUser(id);
		GameResponse gameResponse = GameResponse.fromGame(game, modelMapper);
		return ResponseEntity.ok(gameResponse);
	}
}
