package com.albiosz.honeycombs.turn;

import com.albiosz.honeycombs.usergame.UserGame;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Turn {

	@Id
	@SequenceGenerator(
			name = "turn_id_seq",
			sequenceName = "turn_id_seq",
			allocationSize = 1
	)
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "turn_id_seq"
	)
	@Column(
			nullable = false,
			columnDefinition = "INT"
	)
	private int id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	@JoinColumn(name = "game_id", referencedColumnName = "game_id")
	private UserGame userGame;

	@Column(
			nullable = false,
			columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
	)
	private Instant createdAt;

	@Column(
			nullable = false,
			columnDefinition = "INT"
	)
	private int points;

	public Turn(int points) {
		this.points = points;
		this.createdAt = Instant.now();
	}

	public Turn(UserGame userGame, int points) {
		this(points);
		this.userGame = userGame;
	}
}
