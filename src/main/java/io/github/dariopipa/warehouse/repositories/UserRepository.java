package io.github.dariopipa.warehouse.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.enums.RolesEnum;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);

	@Query("""
			  select distinct u.email
			  from User u
			  join u.roles r
			  where r.role = :role
			""")
	List<String> findEmailsByRole(@Param("role") RolesEnum role);

}
