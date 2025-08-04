package io.github.dariopipa.warehouse.dtos.requests;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterUserDTO {

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Size(max = 50, message = "Email must be at most 50 characters")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
	private String password;

	@NotEmpty(message = "At least one role must be specified")
	private Set<@NotBlank(message = "Role cannot be blank") String> roles;

	public RegisterUserDTO() {

	}

	/**
	 * @param username
	 * @param email
	 * @param password
	 * @param roles
	 */
	public RegisterUserDTO(
			@NotBlank(message = "Username is required") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String username,
			@NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 50, message = "Email must be at most 50 characters") String email,
			@NotBlank(message = "Password is required") @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters") String password,
			@NotBlank(message = "Role cannot be empty") Set<String> roles) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

}
