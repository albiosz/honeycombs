package com.albiosz.honeycombs.usergame;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class UserGameId implements Serializable {

	@Column(name = "user_id")
	private UUID userId;

	@Column(name = "game_id")
	private long gameId;

	public UserGameId(UUID userId, long gameId) {
		this.userId = userId;
		this.gameId = gameId;
	}
}
