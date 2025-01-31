package com.albiosz.honeycombs.usergame;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.turn.Turn;
import com.albiosz.honeycombs.user.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class UserGame {
	@EmbeddedId
	private UserGameId id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@MapsId("gameId")
	@JoinColumn(name = "game_id")
	private Game game;

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

	@OneToMany(
			mappedBy = "userGame",
			cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
			fetch = FetchType.EAGER
	)
	private List<Turn> turns = new ArrayList<>();

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

	public void addTurn(Turn turn) {
		turns.add(turn);
		turn.setUserGame(this);
	}
}
