package io.github.dariopipa.warehouse.integration.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.dariopipa.warehouse.dtos.requests.LoginRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.JwtResponse;
import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;
import io.github.dariopipa.warehouse.entities.AuditLog;
import io.github.dariopipa.warehouse.repositories.AuditLogRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuditLogControllerIT {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private AuditLogRepository auditLogRepository;

	@Autowired
	private ProductTypeService productTypeService;

	private String adminJwtToken;
	private Long testUserId = 1L;

	@BeforeEach
	void setUp() {
		adminJwtToken = getJwtToken("admin", "admin123");
	}

	@AfterEach
	void cleanUp() {
		auditLogRepository.deleteAll();
	}

	@Test
	void test_GetAuditLogs_WithAdminRole_ShouldReturnPaginatedResults() {
		createProductTypeViaService("TestAuditType-" + System.currentTimeMillis());
		createProductTypeViaService("TestAuditType2-" + System.currentTimeMillis());

		HttpHeaders headers = createAuthHeaders(adminJwtToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<PaginatedResponse<AuditLog>> response = restTemplate.exchange(
				"/api/v1/audit-logs?page=0&size=10&sortBy=created_at&direction=desc", HttpMethod.GET, entity,
				new ParameterizedTypeReference<PaginatedResponse<AuditLog>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		PaginatedResponse<AuditLog> responseBody = response.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody.getData()).isNotNull();
		assertThat(responseBody.getData()).hasSizeGreaterThanOrEqualTo(2);

		boolean hasProductTypeCreate = responseBody.getData().stream()
				.anyMatch(log -> log.getAction().toString().equals("CREATE")
						&& log.getEntityType().toString().equals("PRODUCT_TYPE"));
		assertThat(hasProductTypeCreate).isTrue();
	}

	@Test
	void test_GetAuditLogs_WithoutAdminRole_ShouldReturn403() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/audit-logs?page=0&size=10", HttpMethod.GET,
				entity, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void test_AuditLogCreation_WhenProductTypeCreated_ShouldCreateAuditLog() {
		auditLogRepository.deleteAll();

		String productTypeName = "TestAuditType-" + System.currentTimeMillis();
		Long productTypeId = createProductTypeViaService(productTypeName);

		List<AuditLog> auditLogs = auditLogRepository.findAll();
		assertThat(auditLogs).hasSize(1);

		AuditLog auditLog = auditLogs.get(0);
		assertThat(auditLog.getAction().toString()).isEqualTo("CREATE");
		assertThat(auditLog.getEntityType().toString()).isEqualTo("PRODUCT_TYPE");
		assertThat(auditLog.getEntityId()).isEqualTo(productTypeId);
		assertThat(auditLog.getUserId()).isEqualTo(testUserId);
		assertThat(auditLog.getDetails()).contains("product_type");
	}

	@Test
	void test_AuditLogFiltering_ByAction_ShouldOnlyReturnCorrectlyFilteredLogs() {
		createProductTypeViaService("FilterTestType-" + System.currentTimeMillis());

		HttpHeaders headers = createAuthHeaders(adminJwtToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<PaginatedResponse<AuditLog>> response = restTemplate.exchange(
				"/api/v1/audit-logs?page=0&size=10", HttpMethod.GET, entity,
				new ParameterizedTypeReference<PaginatedResponse<AuditLog>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		PaginatedResponse<AuditLog> responseBody = response.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody.getData()).isNotNull();

		boolean allAreCreateActions = responseBody.getData().stream()
				.allMatch(log -> log.getAction().toString().equals("CREATE"));
		assertThat(allAreCreateActions).isTrue();
	}

	private HttpHeaders createAuthHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);
		return headers;
	}

	private String getJwtToken(String username, String password) {
		LoginRequestDTO loginRequest = new LoginRequestDTO();
		loginRequest.setUsername(username);
		loginRequest.setPassword(password);

		ResponseEntity<JwtResponse> response = restTemplate.postForEntity("/api/v1/auth/login", loginRequest,
				JwtResponse.class);

		if (response.getBody() != null) {
			return response.getBody().getToken();
		}
		throw new RuntimeException("Failed to authenticate");
	}

	private Long createProductTypeViaService(String name) {
		ProductTypesDTO productTypeDTO = new ProductTypesDTO();
		productTypeDTO.setName(name);
		return productTypeService.save(productTypeDTO, testUserId);
	}
}