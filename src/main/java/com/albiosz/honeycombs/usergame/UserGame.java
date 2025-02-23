package com.albiosz.honeycombs.usergame;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.turn.Turn;
import com.albiosz.honeycombs.user.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserGame implements Serializable {
	@EmbeddedId
	private UserGameId id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.RESTRICT)
	private User user;

	@ManyToOne
	@MapsId("gameId")
	@JoinColumn(name = "game_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
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
			cascade = CascadeType.ALL,
			fetch = FetchType.EAGER
	)
	private List<Turn> turns = new ArrayList<>();

	public UserGame(User user, Game game, boolean isUserHost) {
		this.id = new UserGameId(user.getId(), game.getId());
		this.user = user;
		this.game = game;
		this.createdAt = Instant.now();
		this.playerNo = 0;
		this.isUserHost = isUserHost;
		this.isUsersTurn = false;
		this.state = State.IN_LOBBY;
	}

	public void addTurn(Turn turn) {
		turns.add(turn);
		turn.setUserGame(this);
	}
}
