package com.albiosz.honeycombs;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.GameRepository;
import com.albiosz.honeycombs.turn.Turn;
import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.user.UserRepository;
import com.albiosz.honeycombs.usergame.UserGame;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy
public class DBConfig {

	private final UserRepository userRepo;
	private final GameRepository gameRepo;
	private final PasswordEncoder passwordEncoder;

	public DBConfig(
			UserRepository userRepo,
			GameRepository gameRepo,
			PasswordEncoder passwordEncoder
	) {
		this.userRepo = userRepo;
		this.gameRepo = gameRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void initDB() {
		User user = new User("email@email.com", passwordEncoder.encode("password"), "nickname", true);
		User createdUser = userRepo.save(user);

		Game game = new Game();
		Game createdGame = gameRepo.save(game);


		Turn turn1 = new Turn(5);
		Turn turn2 = new Turn(6);

		createdUser.joinGame(createdGame, true);
		List<UserGame> userGame = createdUser.getUserGames();

		userGame.getFirst().addTurn(turn1);
		userGame.getFirst().addTurn(turn2);
	}
}
