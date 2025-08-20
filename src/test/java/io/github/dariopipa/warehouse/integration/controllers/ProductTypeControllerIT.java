package io.github.dariopipa.warehouse.integration.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

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
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.repositories.ProductTypeRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class ProductTypeControllerIT {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ProductTypeRepository productTypeRepository;

	@Autowired
	private ProductTypeService productTypeService;

	private String jwtToken;
	private Long testUserId = 1L;

	@BeforeEach
	void setUp() {
		productTypeRepository.deleteAll();
		jwtToken = authenticateAndGetToken();
	}

	@AfterEach
	void tearDown() {
		productTypeRepository.deleteAll();
	}

	@Test
	void test_CreateProductType_ShouldSaveToDatabase() {
		String uniqueName = "TestElectronics-" + System.currentTimeMillis();

		ProductTypesDTO request = new ProductTypesDTO();
		request.setName(uniqueName);

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<ProductTypesDTO> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/api/v1/product-types", HttpMethod.POST, entity,
				Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		List<ProductType> allProductTypes = productTypeRepository.findAll();
		Optional<ProductType> savedProductType = allProductTypes.stream().filter(pt -> uniqueName.equals(pt.getName()))
				.findFirst();
		assertThat(savedProductType).isPresent();
		assertThat(savedProductType.get().getName()).isEqualTo(uniqueName);
		assertThat(savedProductType.get().getCreatedBy()).isEqualTo(testUserId);
	}

	@Test
	void test_GetProductTypes_ShouldReturnPagedResults() {
		String name1 = "TestElectronics-" + System.currentTimeMillis();
		String name2 = "TestBooks-" + (System.currentTimeMillis() + 1);

		createProductTypeViaService(name1);
		createProductTypeViaService(name2);

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<PaginatedResponse<ProductTypeResponseDTO>> response = restTemplate.exchange(
				"/api/v1/product-types?page=0&size=10&sortBy=name&direction=asc", HttpMethod.GET, entity,
				new ParameterizedTypeReference<PaginatedResponse<ProductTypeResponseDTO>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		PaginatedResponse<ProductTypeResponseDTO> responseBody = response.getBody();
		assertThat(responseBody.getData()).hasSize(2);
		assertThat(responseBody.getData()).anyMatch(pt -> pt.getName().equals(name1));
		assertThat(responseBody.getData()).anyMatch(pt -> pt.getName().equals(name2));
	}

	@Test
	void test_GetProductTypeByID_ShouldReturnProductType() {
		String uniqueName = "TestGaming-" + System.currentTimeMillis();
		Long productTypeId = createProductTypeViaService(uniqueName);

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<ProductTypeResponseDTO> response = restTemplate.exchange(
				"/api/v1/product-types/" + productTypeId, HttpMethod.GET, entity, ProductTypeResponseDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		ProductTypeResponseDTO responseBody = response.getBody();
		assertThat(responseBody.getName()).isEqualTo(uniqueName);
		assertThat(responseBody.getId()).isEqualTo(productTypeId);
	}

	@Test
	void test_UpdateProductType_ShouldUpdateDatabaseRecord() {
		String originalName = "TestElectronics-" + System.currentTimeMillis();
		Long productTypeId = createProductTypeViaService(originalName);

		ProductTypesDTO request = new ProductTypesDTO();
		request.setName("Consumer Electronics");

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<ProductTypesDTO> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/api/v1/product-types/" + productTypeId,
				HttpMethod.PATCH, entity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		Optional<ProductType> updatedProductType = productTypeRepository.findById(productTypeId);
		assertThat(updatedProductType).isPresent();
		assertThat(updatedProductType.get().getName()).isEqualTo("Consumer Electronics");
		assertThat(updatedProductType.get().getUpdatedBy()).isEqualTo(testUserId);
	}

	@Test
	void test_DeleteProductType_ShouldSoftDeleteFromDatabase() {
		String uniqueName = "TestObsoleteCategory-" + System.currentTimeMillis();
		Long productTypeId = createProductTypeViaService(uniqueName);

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<Void> response = restTemplate.exchange("/api/v1/product-types/" + productTypeId,
				HttpMethod.DELETE, entity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		Optional<ProductType> deletedProductType = productTypeRepository.findById(productTypeId);
		assertThat(deletedProductType).isEmpty();
	}

	@Test
	void test_CreateProductType_WithDuplicateName_ShouldReturnConflictException() {
		String uniqueName = "TestElectronics-" + System.currentTimeMillis();
		createProductTypeViaService(uniqueName);

		ProductTypesDTO request = new ProductTypesDTO();
		request.setName(uniqueName);
		HttpHeaders headers = createAuthHeaders();
		HttpEntity<ProductTypesDTO> entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/product-types", HttpMethod.POST, entity,
				String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void test_CallTheProductTypeEndpoint_WithoutAuthentication_ShouldReturnUnauthorized() {
		ProductTypesDTO request = new ProductTypesDTO();
		request.setName("Test Category");

		HttpEntity<ProductTypesDTO> entity = new HttpEntity<>(request);

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/product-types", HttpMethod.POST, entity,
				String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	private HttpHeaders createAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(jwtToken);
		return headers;
	}

	private String authenticateAndGetToken() {
		LoginRequestDTO loginRequest = new LoginRequestDTO();
		loginRequest.setUsername("admin");
		loginRequest.setPassword("admin123");

		ResponseEntity<JwtResponse> response = restTemplate.postForEntity("/api/v1/auth/login", loginRequest,
				JwtResponse.class);

		JwtResponse responseBody = response.getBody();
		if (responseBody != null) {
			return responseBody.getToken();
		}
		throw new RuntimeException("Failed to authenticate");
	}

	private Long createProductTypeViaService(String name) {
		ProductTypesDTO productTypeDTO = new ProductTypesDTO();
		productTypeDTO.setName(name);
		return productTypeService.save(productTypeDTO, testUserId);
	}
}
