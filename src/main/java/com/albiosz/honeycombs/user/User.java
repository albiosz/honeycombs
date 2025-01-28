package com.albiosz.honeycombs.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

	@Id
	@SequenceGenerator(
			name = "user_id_seq",
			sequenceName = "user_id_seq",
			allocationSize = 1
	)
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "user_id_seq"
	)
	@Column(
			updatable = false,
			nullable = false,
			columnDefinition = "serial"
	)
	private long id;

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

	public User(String email, String password, String nickname) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
	}
}
