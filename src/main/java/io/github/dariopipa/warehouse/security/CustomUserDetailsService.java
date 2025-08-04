package io.github.dariopipa.warehouse.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(
						"User not found: " + username));

		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername()).password(user.getPassword())
				.authorities(user.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority(
								role.getRole().name()))
						.collect(Collectors.toList()))
				.build();
	}
}