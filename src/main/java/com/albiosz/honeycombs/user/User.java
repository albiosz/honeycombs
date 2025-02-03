package com.albiosz.honeycombs.user;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.usergame.UserGame;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User implements UserDetails {

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
	private String username; // username = email

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

	@Column
	private boolean isEnabled;

	@Column
	private String verificationCode;

	@Column
	private Instant verificationCodeExpiresAt;

	@OneToMany(
			mappedBy = "user", // it is a field in the UserGame class
			orphanRemoval = false, // when a user is removed, all the userGames stay
			cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
	)
	private List<UserGame> userGames = new ArrayList<>();

	public User(String username, String password, String nickname, boolean isEnabled) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.isEnabled = isEnabled;
	}

	public void addUserToGame(Game game) {
		this.userGames.add(new UserGame(this, game));
	}

	public boolean isVerificationCodeExpired() {
		return verificationCodeExpiresAt.isBefore(Instant.now());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	//TODO: add proper boolean checks
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}
}
