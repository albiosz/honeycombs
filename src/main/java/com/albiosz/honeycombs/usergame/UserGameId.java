package com.albiosz.honeycombs.usergame;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class UserGameId implements Serializable {

	@Column(name = "student_id")
	private Long userId;

	@Column(name = "game_id")
	private Long gameId;

	public UserGameId(Long userId, Long gameId) {
		this.userId = userId;
		this.gameId = gameId;
	}
}
