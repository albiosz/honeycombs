package com.albiosz.honeycombs.user;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.usergame.UserGame;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

	@Id
	@GeneratedValue(
			strategy = GenerationType.UUID
	)
	@Column(
			updatable = false,
			nullable = false,
			columnDefinition = "UUID"
	)
	private UUID id;

	@Column(
			nullable = false,
			columnDefinition = "TEXT",
			unique = true
	)
	private String email;

	@Column(
			nullable = false,
			columnDefinition = "TEXT"
	)
	private String password;

	@Column(
			nullable = false,
			columnDefinition = "TEXT",
			unique = true
	)
	private String nickname;

	@OneToMany(
			mappedBy = "user", // it is a field in the UserGame class
			orphanRemoval = false, // when a user is removed, all the userGames stay
			cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
	)
	private List<UserGame> userGames = new ArrayList<>();

	public User(String email, String password, String nickname) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
	}

	public void addUserToGame(Game game) {
		this.userGames.add(new UserGame(this, game));
	}
}
