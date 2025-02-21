package com.albiosz.honeycombs.game;

import com.albiosz.honeycombs.user.User;
import com.albiosz.honeycombs.usergame.UserGame;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Game implements Serializable {

	@Id
	@SequenceGenerator(
			name = "game_id_seq",
			sequenceName = "game_id_seq",
			allocationSize = 1
	)
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "game_id_seq"
	)
	@Column(
			updatable = false,
			nullable = false,
			columnDefinition = "BIGINT"
	)
	private long id;

	@Column(
			nullable = false,
			columnDefinition = "TEXT"
	)
	private String name;

	@Column(
			nullable = false,
			columnDefinition = "timestamp without time zone"
	)
	private Instant createdAt;

	@Enumerated(EnumType.STRING)
	@Column(
			nullable = false,
			columnDefinition = "TEXT"
	)
	private State state;

	@OneToMany(
			mappedBy = "game",
			orphanRemoval = true,
			cascade = CascadeType.ALL,
			fetch = FetchType.EAGER
	)
//	@JsonManagedReference
	private Map<UUID, UserGame> userGames = new HashMap<>();

	public Game() {
		this.name = "NO_NAME_GAME";
		this.createdAt = Instant.now();
		this.state = State.CREATED;
	}

	public Game(String name) {
		this.name = name;
		this.createdAt = Instant.now();
		this.state = State.CREATED;
	}

	public UserGame addUserToGame(User user) {
		UserGame userGame = new UserGame(user, this);
		userGames.put(user.getId(), userGame);
		user.getUserGames().add(userGame);
		return userGame;
	}

	public UserGame getUserGame(UUID userId) {
		return userGames.get(userId);
	}
}

