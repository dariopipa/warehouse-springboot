package io.github.dariopipa.warehouse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.dariopipa.warehouse.audit.AuditLogger;
import io.github.dariopipa.warehouse.dtos.requests.RegisterUserDTO;
import io.github.dariopipa.warehouse.entities.Roles;
import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.enums.RolesEnum;
import io.github.dariopipa.warehouse.exceptions.InvalidRoleException;
import io.github.dariopipa.warehouse.repositories.RoleRepository;
import io.github.dariopipa.warehouse.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuditLogger auditLogger;

	@InjectMocks
	private AuthServiceImpl authService;

	private RegisterUserDTO registerUserDTO;
	private User user;
	private Roles userRole;
	private Roles adminRole;

	@BeforeEach
	void setUp() {
		registerUserDTO = new RegisterUserDTO();
		registerUserDTO.setUsername("testuser");
		registerUserDTO.setEmail("test@example.com");
		registerUserDTO.setPassword("password123");

		user = new User("testuser", "test@example.com", "encodedPassword");
		user.setId(1L);

		userRole = new Roles();
		userRole.setId(1L);
		userRole.setRole(RolesEnum.ROLE_USER);

		adminRole = new Roles();
		adminRole.setId(2L);
		adminRole.setRole(RolesEnum.ROLE_ADMIN);
	}

	@Test
	void testRegisterNewUser_WithNoRoles_ShouldAssignDefaultRole() {
		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(roleRepository.findByRole(RolesEnum.ROLE_USER)).thenReturn(Optional.of(userRole));
		when(userRepository.save(any(User.class))).thenReturn(user);

		User result = authService.registerNewUser(registerUserDTO, 1L);

		assertEquals(user, result);
		verify(userRepository).save(any(User.class));
		verify(auditLogger).log(1L, AuditAction.CREATE, EntityType.USER, 1L);
	}

	@Test
	void testRegisterNewUser_WithValidRoles_ShouldAssignSpecifiedRoles() {
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_ADMIN");
		registerUserDTO.setRoles(roles);

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(roleRepository.findByRole(RolesEnum.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
		when(userRepository.save(any(User.class))).thenReturn(user);

		User result = authService.registerNewUser(registerUserDTO, 1L);

		assertEquals(user, result);
		verify(userRepository).save(any(User.class));
		verify(auditLogger).log(1L, AuditAction.CREATE, EntityType.USER, 1L);
	}

	@Test
	void testRegisterNewUser_WithInvalidRole_ShouldThrowException() {
		Set<String> roles = new HashSet<>();
		roles.add("INVALID_ROLE");
		registerUserDTO.setRoles(roles);

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

		assertThrows(InvalidRoleException.class, () -> {
			authService.registerNewUser(registerUserDTO, 1L);
		});
	}

	@Test
	void testRegisterNewUser_WithNonExistentRole_ShouldThrowException() {
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_ADMIN");
		registerUserDTO.setRoles(roles);

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(roleRepository.findByRole(RolesEnum.ROLE_ADMIN)).thenReturn(Optional.empty());

		assertThrows(InvalidRoleException.class, () -> {
			authService.registerNewUser(registerUserDTO, 1L);
		});
	}

	@Test
	void testRegisterNewUser_WhenDefaultRoleNotFound_ShouldThrowException() {
		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(roleRepository.findByRole(RolesEnum.ROLE_USER)).thenReturn(Optional.empty());

		assertThrows(InvalidRoleException.class, () -> {
			authService.registerNewUser(registerUserDTO, 1L);
		});
	}

	@Test
	void testFindEmailsByRole_ShouldReturnEmails() {
		List<String> expectedEmails = List.of("admin1@example.com", "admin2@example.com");
		when(userRepository.findEmailsByRole(RolesEnum.ROLE_ADMIN)).thenReturn(expectedEmails);

		List<String> result = authService.findEmailsByRole(RolesEnum.ROLE_ADMIN);

		assertEquals(expectedEmails, result);
		verify(userRepository).findEmailsByRole(RolesEnum.ROLE_ADMIN);
	}

	@Test
	void testRegisterNewUser_WithEmptyRoles_ShouldAssignDefaultRole() {
		registerUserDTO.setRoles(new HashSet<>());

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(roleRepository.findByRole(RolesEnum.ROLE_USER)).thenReturn(Optional.of(userRole));
		when(userRepository.save(any(User.class))).thenReturn(user);

		User result = authService.registerNewUser(registerUserDTO, 1L);

		assertEquals(user, result);
		verify(userRepository).save(any(User.class));
		verify(auditLogger).log(1L, AuditAction.CREATE, EntityType.USER, 1L);
	}

	@Test
	void testRegisterNewUser_WithMultipleRoles_ShouldAssignAllRoles() {
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_USER");
		roles.add("ROLE_ADMIN");
		registerUserDTO.setRoles(roles);

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(roleRepository.findByRole(RolesEnum.ROLE_USER)).thenReturn(Optional.of(userRole));
		when(roleRepository.findByRole(RolesEnum.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
		when(userRepository.save(any(User.class))).thenReturn(user);

		User result = authService.registerNewUser(registerUserDTO, 1L);

		assertEquals(user, result);
		verify(userRepository).save(any(User.class));
		verify(auditLogger).log(1L, AuditAction.CREATE, EntityType.USER, 1L);
	}
}
