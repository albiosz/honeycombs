package com.albiosz.honeycombs.user;

import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByUsername(String username);

	@Transactional
	@Modifying
	@Query("DELETE FROM User u WHERE u.id = :id")
	void deleteById(UUID id);

	@Transactional
	@Modifying
	@Query("DELETE FROM User")
	void deleteAll();
}
