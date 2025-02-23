package com.albiosz.honeycombs.game;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
	List<Game> findByState(State state);

	@Transactional
	@Modifying
	@Query("DELETE FROM Game g WHERE g.id = :id")
	void deleteById(long id);

	@Transactional
	@Modifying
	@Query("DELETE FROM Game")
	void deleteAll();
}
