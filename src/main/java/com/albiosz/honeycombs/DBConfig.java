package com.albiosz.honeycombs;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.turn.Turn;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.usergame.UserGame;
import com.albiosz.honeycombs.usergame.UserGameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DBConfig {

	private final UserRepository userRepo;
	private final GameRepository gameRepo;

	public DBConfig(
			UserRepository userRepo,
			GameRepository gameRepo
	) {
		this.userRepo = userRepo;
		this.gameRepo = gameRepo;
	}

	@Transactional
	public void initDB() {
		User user = new User("email@email.com", "password", "nickname");
		User createdUser = userRepo.save(user);

		Game game = new Game();
		Game createdGame = gameRepo.save(game);


		Turn turn1 = new Turn(5);
		Turn turn2 = new Turn(6);

		UserGame userGame = new UserGame(createdUser, createdGame);
		userGame.addTurn(turn1);
		userGame.addTurn(turn2);

		createdUser.addUserToGame(userGame);

		createdUser.setEmail("new@email.com");

		System.out.println("User: " + createdUser);
	}
}
