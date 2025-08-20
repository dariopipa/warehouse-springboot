package io.github.dariopipa.warehouse.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.github.dariopipa.warehouse.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expirationMs}")
	private long jwtExpirationMs;

	private SecretKey key;

	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateJwtToken(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getId();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder().subject(user.getUsername()).claim("id", userId).issuedAt(now).expiration(expiryDate)
				.signWith(key).compact();
	}

	private Claims parseToken(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	public String getUsernameFromToken(String token) {
		return parseToken(token).getSubject();
	}

	public Date getExpirationDate(String token) {
		return parseToken(token).getExpiration();
	}

	public boolean isTokenExpired(String token) {
		Date expiration = getExpirationDate(token);
		return expiration.before(new Date());
	}

	public boolean validateToken(String token, String username) {
		try {
			String tokenUser = getUsernameFromToken(token);
			return (username.equals(tokenUser) && !isTokenExpired(token));
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}