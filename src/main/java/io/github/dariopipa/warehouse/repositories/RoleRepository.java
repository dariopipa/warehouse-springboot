package io.github.dariopipa.warehouse.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.dariopipa.warehouse.entities.Roles;
import io.github.dariopipa.warehouse.enums.RolesEnum;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
	Optional<Roles> findByRole(RolesEnum role);
}
