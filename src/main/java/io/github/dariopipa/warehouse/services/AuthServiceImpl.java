package io.github.dariopipa.warehouse.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.dtos.requests.RegisterUserDTO;
import io.github.dariopipa.warehouse.entities.Roles;
import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.enums.RolesEnum;
import io.github.dariopipa.warehouse.repositories.RoleRepository;
import io.github.dariopipa.warehouse.repositories.UserRepository;
import io.github.dariopipa.warehouse.services.interfaces.AuthService;
import io.github.dariopipa.warehouse.utils.JwtUtils;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	public AuthServiceImpl(UserRepository userRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User registerNewUser(RegisterUserDTO request) {
		User user = new User(request.getUsername(), request.getEmail(),
				passwordEncoder.encode(request.getPassword()));

		Set<Roles> roles = assignUserRoles(request.getRoles());
		user.setRoles(roles);

		return userRepository.save(user);
	}

	private Set<Roles> assignUserRoles(Set<String> roleNames) {
		Set<Roles> roles = new HashSet<>();

		if (roleNames == null || roleNames.isEmpty()) {
			roles.add(getDefaultRole());
		} else {
			for (String roleName : roleNames) {
				try {
					RolesEnum roleEnum = RolesEnum.valueOf(roleName);
					Roles role = roleRepository.findByRole(roleEnum)
							.orElseThrow(() -> new RuntimeException(
									"Role not found: " + roleName));
					roles.add(role);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(
							"Invalid role name: " + roleName);
				}
			}
		}
		return roles;
	}

	private Roles getDefaultRole() {
		return roleRepository.findByRole(RolesEnum.ROLE_USER).orElseThrow(
				() -> new RuntimeException("Default role not found"));
	}
}
