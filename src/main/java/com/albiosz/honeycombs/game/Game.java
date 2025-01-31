package com.albiosz.honeycombs.game;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
public class Game {

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
			columnDefinition = "timestamp without time zone"
	)
	private Instant createdAt;

	@Enumerated(EnumType.STRING)
	@Column(
			nullable = false,
			columnDefinition = "TEXT"
	)
	private State state;

	public Game() {
		this.createdAt = Instant.now();
		this.state = State.CREATED;
	}
}

