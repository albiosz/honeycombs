package com.albiosz.honeycombs.usergame;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGameRepository extends JpaRepository<UserGame, UserGameId> {
}
