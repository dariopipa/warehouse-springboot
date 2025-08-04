package io.github.dariopipa.warehouse.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.dariopipa.warehouse.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
}
