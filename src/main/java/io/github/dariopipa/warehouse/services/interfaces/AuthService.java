package io.github.dariopipa.warehouse.services.interfaces;

import io.github.dariopipa.warehouse.dtos.requests.RegisterUserDTO;
import io.github.dariopipa.warehouse.entities.User;

public interface AuthService {

	User registerNewUser(RegisterUserDTO request);
}
