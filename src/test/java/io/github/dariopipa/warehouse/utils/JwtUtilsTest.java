package io.github.dariopipa.warehouse.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.dariopipa.warehouse.entities.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class JwtUtilsTest {

	@Autowired
	private JwtUtils jwtUtils;

	private UsernamePasswordAuthenticationToken authenticatedUserToken;

	private String validJwtToken;

	private static final String TEST_USERNAME = "testuser";

	private static final Long TEST_USER_ID = 1L;

	@BeforeEach
	void setUp() {
		User user = new User();
		user.setId(TEST_USER_ID);
		user.setUsername(TEST_USERNAME);
		authenticatedUserToken = new UsernamePasswordAuthenticationToken(user, null, null);
		validJwtToken = jwtUtils.generateJwtToken(authenticatedUserToken);
	}

	private UsernamePasswordAuthenticationToken createAuthenticationTokenForUser(Long id, String username) {
		User user = new User();
		user.setId(id);
		user.setUsername(username);
		return new UsernamePasswordAuthenticationToken(user, null, null);
	}

	@Test
	void test_GenerateToken_ContainsUsername() {
		assertEquals(TEST_USERNAME, jwtUtils.getUsernameFromToken(validJwtToken));
	}

	@Test
	void test_ValidateToken_ReturnsTrueForMatchingUser() {
		assertTrue(jwtUtils.validateToken(validJwtToken, TEST_USERNAME));
	}

	@Test
	void test_TokenExpiry_NotExpiredImmediatelyAfterGeneration() {
		assertFalse(jwtUtils.isTokenExpired(validJwtToken));
	}

	@ParameterizedTest
	@CsvSource({
			"wronguser, Token should be invalid for a mismatched username.",
			"otheruser, Token should be invalid for a completely different username.",
			"'', Token should be invalid for empty username.",
			"admin, Token should be invalid for different valid username."
	})
	void test_ValidateToken_ReturnsFalseForWrongUser(String mismatchedUsername, String message) {
		assertFalse(jwtUtils.validateToken(validJwtToken, mismatchedUsername), message);
	}

	@Test
	void test_ValidateToken_ReturnsFalseForTamperedToken() {
		String originalToken = jwtUtils.generateJwtToken(createAuthenticationTokenForUser(8L, "tamper"));
		String[] parts = originalToken.split("\\.");
		assertEquals(3, parts.length);
		String tamperedToken = parts[0] + "." + parts[1] + ".tampered_signature";

		assertFalse(jwtUtils.validateToken(tamperedToken, "tamper"));
	}

	@ParameterizedTest
	@CsvSource({
			"still.not.a.jwt, false",
			"random-string, false"
	})
	void test_ValidateToken_ReturnsFalseForInvalidTokens(String invalidToken, boolean expectedResult) {
		assertEquals(expectedResult, jwtUtils.validateToken(invalidToken, "anyusername"));
	}

	@Test
	void test_GenerateToken_DifferentUsersHaveDifferentTokens() {
		UsernamePasswordAuthenticationToken user1Token = createAuthenticationTokenForUser(1L, "user1");
		UsernamePasswordAuthenticationToken user2Token = createAuthenticationTokenForUser(2L, "user2");

		String token1 = jwtUtils.generateJwtToken(user1Token);
		String token2 = jwtUtils.generateJwtToken(user2Token);

		assertFalse(token1.equals(token2));
		assertEquals("user1", jwtUtils.getUsernameFromToken(token1));
		assertEquals("user2", jwtUtils.getUsernameFromToken(token2));
	}

	@Test
	void test_TokenExpiration_DateIsInFuture() {
		Date expirationDate = jwtUtils.getExpirationDate(validJwtToken);
		Date now = new Date();

		assertTrue(expirationDate.after(now));
		long timeDiff = expirationDate.getTime() - now.getTime();
		assertTrue(timeDiff > 3500000 && timeDiff <= 3600000);
	}

}