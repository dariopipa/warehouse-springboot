package io.github.dariopipa.warehouse.services.interfaces;

import java.util.List;

import org.springframework.data.repository.query.Param;

import io.github.dariopipa.warehouse.dtos.requests.RegisterUserDTO;
import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.enums.RolesEnum;

public interface AuthService {

	User registerNewUser(RegisterUserDTO request, Long loggedInUser);

	List<String> findEmailsByRole(@Param("role") RolesEnum role);
}
