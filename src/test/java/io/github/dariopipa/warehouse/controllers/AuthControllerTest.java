package io.github.dariopipa.warehouse.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.github.dariopipa.warehouse.dtos.requests.LoginRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.RegisterUserDTO;
import io.github.dariopipa.warehouse.dtos.responses.JwtResponse;
import io.github.dariopipa.warehouse.entities.Roles;
import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.enums.RolesEnum;
import io.github.dariopipa.warehouse.exceptions.UserAlreadyExistsException;
import io.github.dariopipa.warehouse.repositories.UserRepository;
import io.github.dariopipa.warehouse.services.interfaces.AuthService;
import io.github.dariopipa.warehouse.utils.JwtUtils;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private AuthService authService;

	@Mock
	private JwtUtils jwtUtils;

	@InjectMocks
	private AuthController authController;

	private User loggedInUser;
	private RegisterUserDTO registerUserDTO;
	private LoginRequestDTO loginRequestDTO;
	private User testUser;

	@BeforeEach
	void setUp() {
		loggedInUser = new User();
		loggedInUser.setId(1L);
		loggedInUser.setUsername("admin");

		registerUserDTO = new RegisterUserDTO();
		registerUserDTO.setUsername("newuser");
		registerUserDTO.setEmail("newuser@test.com");
		registerUserDTO.setPassword("password123");
		registerUserDTO.setRoles(Set.of("USER"));

		loginRequestDTO = new LoginRequestDTO();
		loginRequestDTO.setUsername("testuser");
		loginRequestDTO.setPassword("password123");

		testUser = new User();
		testUser.setId(2L);
		testUser.setUsername("testuser");
		testUser.setEmail("testuser@test.com");

		Roles userRole = new Roles();
		userRole.setRole(RolesEnum.ROLE_USER);
		testUser.setRoles(Set.of(userRole));
	}

	@Test
	void test_registerUser_ShouldCallService_WhenTheDTOIsValid() {
		when(userRepository.existsByUsername(registerUserDTO.getUsername())).thenReturn(false);
		when(userRepository.existsByEmail(registerUserDTO.getEmail())).thenReturn(false);
		when(authService.registerNewUser(registerUserDTO, loggedInUser.getId())).thenReturn(testUser);

		assertThrows(IllegalStateException.class, () -> {
			authController.registerUser(registerUserDTO, loggedInUser);
		});

		verify(userRepository).existsByUsername(registerUserDTO.getUsername());
		verify(userRepository).existsByEmail(registerUserDTO.getEmail());
		verify(authService).registerNewUser(registerUserDTO, loggedInUser.getId());
	}

	@Test
	void test_registerUser_ShouldThrowException_WhenUserWithUsernameExists() {
		when(userRepository.existsByUsername(registerUserDTO.getUsername())).thenReturn(true);

		assertThrows(UserAlreadyExistsException.class, () -> {
			authController.registerUser(registerUserDTO, loggedInUser);
		});

		verify(userRepository).existsByUsername(registerUserDTO.getUsername());
	}

	@Test
	void test_registerUser_ShouldThrowException_WhenUserWithEmailExists() {
		when(userRepository.existsByUsername(registerUserDTO.getUsername())).thenReturn(false);
		when(userRepository.existsByEmail(registerUserDTO.getEmail())).thenReturn(true);

		assertThrows(UserAlreadyExistsException.class, () -> {
			authController.registerUser(registerUserDTO, loggedInUser);
		});

		verify(userRepository).existsByUsername(registerUserDTO.getUsername());
		verify(userRepository).existsByEmail(registerUserDTO.getEmail());
	}

	@Test
	void test_authenticateUser_ShouldReturnJwtResponse_WithCorrectCredentials() {
		Authentication authentication = mock(Authentication.class);
		String jwtToken = "test-jwt-token";
		List<String> roles = Arrays.asList("ROLE_USER");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(testUser);
		when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwtToken);

		ResponseEntity<JwtResponse> response = authController.authenticateUser(loginRequestDTO);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		JwtResponse responseBody = response.getBody();
		assertNotNull(responseBody);
		assertEquals(jwtToken, responseBody.getToken());
		assertEquals(testUser.getUsername(), responseBody.getUsername());
		assertEquals(roles, responseBody.getRoles());

		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(jwtUtils).generateJwtToken(authentication);
	}

	@Test
	void test_authenticateUser_ShouldReturnAllTheRoles() {
		Authentication authentication = mock(Authentication.class);
		String jwtToken = "test-jwt-token";

		Roles adminRole = new Roles();
		adminRole.setRole(RolesEnum.ROLE_ADMIN);
		Roles managerRole = new Roles();
		managerRole.setRole(RolesEnum.ROLE_MANAGER);

		User multiRoleUser = new User();
		multiRoleUser.setId(3L);
		multiRoleUser.setUsername("adminuser");
		multiRoleUser.setRoles(Set.of(adminRole, managerRole));

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(multiRoleUser);
		when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwtToken);

		ResponseEntity<JwtResponse> response = authController.authenticateUser(loginRequestDTO);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		JwtResponse responseBody = response.getBody();
		assertNotNull(responseBody);
		assertEquals(jwtToken, responseBody.getToken());
		assertEquals(multiRoleUser.getUsername(), responseBody.getUsername());
		assertEquals(2, responseBody.getRoles().size());

		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(jwtUtils).generateJwtToken(authentication);
	}
}
