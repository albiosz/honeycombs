package com.albiosz.honeycombs.usergame;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.user.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
public class UserGame {
	@EmbeddedId
	private UserGameId id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "student_id")
	User user;

	@ManyToOne
	@MapsId("gameId")
	@JoinColumn(name = "game_id")
	Game game;

	@Column(
			nullable = false,
			columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
	)
	private Instant createdAt;

	@Column(
			nullable = false,
			columnDefinition = "INT"
	)
	private int playerNo;

	@Column(
			nullable = false,
			columnDefinition = "BOOLEAN"
	)
	private boolean isUserHost;

	@Column(
			nullable = false,
			columnDefinition = "BOOLEAN"
	)
	private boolean isUsersTurn;

	@Enumerated(EnumType.STRING)
	@Column(
			nullable = false,
			columnDefinition = "TEXT"
	)
	private State state;

	public UserGame(User user, Game game) {
		this.id = new UserGameId(user.getId(), game.getId());
		this.user = user;
		this.game = game;
		this.createdAt = Instant.now();
		this.playerNo = 0;
		this.isUserHost = true;
		this.isUsersTurn = false;
		this.state = State.ACTIVE;
	}
}
