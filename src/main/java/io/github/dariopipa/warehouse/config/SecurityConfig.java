package io.github.dariopipa.warehouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.dariopipa.warehouse.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth

				// Only login is public
				.requestMatchers("/api/v1/auth/login", "/swagger-ui/**",
						"/swagger-ui.html", "/v3/api-docs/**",
						"/v1/api-docs/**")
				.permitAll()

				// Register requires ADMIN or MANAGER roles
				.requestMatchers("/api/v1/auth/register")
				.hasAnyRole("ADMIN", "MANAGER")

				.requestMatchers("/actuator/**").hasRole("ADMIN")

				// All other endpoints require JWT authentication
				.anyRequest().authenticated())

				.sessionManagement(sess -> sess
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.formLogin(form -> form.disable())
				.httpBasic(basic -> basic.disable());

		http.addFilterBefore(jwtAuthFilter,
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}