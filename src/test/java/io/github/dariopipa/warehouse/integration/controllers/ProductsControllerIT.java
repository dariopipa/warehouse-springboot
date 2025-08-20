package io.github.dariopipa.warehouse.integration.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.LoginRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.JwtResponse;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.enums.OperationsType;
import io.github.dariopipa.warehouse.repositories.ProductRepository;
import io.github.dariopipa.warehouse.repositories.ProductTypeRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class ProductsControllerIT {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductTypeRepository productTypeRepository;

	private ProductType productType;
	private Long testUserId = 1L;
	private String jwtToken;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
		productTypeRepository.deleteAll();

		jwtToken = authenticateAndGetToken();

		productType = new ProductType();
		productType.setName("Electronics-" + System.currentTimeMillis());
		productType.setCreatedBy(testUserId);
		productType.setUpdatedBy(testUserId);
		productType = productTypeRepository.save(productType);
	}

	@AfterEach
	void tearDown() {
		productRepository.deleteAll();
		productTypeRepository.deleteAll();
	}

	@Test
	void test_CreateProduct_ShouldSaveEntityToDatabase() {
		CreateProductDTO request = new CreateProductDTO();
		request.setName("Integration Test Product");
		request.setDescription("A product for integration testing");
		request.setQuantity(100);
		request.setLowStockThreshold(10);
		request.setWeight(1.5);
		request.setHeight(10.0);
		request.setLength(20.0);
		request.setProductTypeId(productType.getId());

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<CreateProductDTO> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/api/v1/products", HttpMethod.POST, entity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getLocation()).isNotNull();

		String locationHeader = response.getHeaders().getLocation().toString();
		String productIdStr = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
		Long productId = Long.parseLong(productIdStr);

		Optional<Product> savedProduct = productRepository.findById(productId);
		assertThat(savedProduct).isPresent();
		assertThat(savedProduct.get().getName()).isEqualTo("Integration Test Product");
		assertThat(savedProduct.get().getQuantity()).isEqualTo(100);
		assertThat(savedProduct.get().getSku()).isNotNull();
		assertThat(savedProduct.get().getProductType().getId()).isEqualTo(productType.getId());
	}

	@Test
	void test_GetProduct_ShouldReturnProductGetOneResponseDTO() {
		Long productId = createProductViaService("Test Product");

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<ProductGetOneResponseDTO> response = restTemplate.exchange("/api/v1/products/" + productId,
				HttpMethod.GET, entity, ProductGetOneResponseDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		ProductGetOneResponseDTO responseBody = response.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody.getName()).isEqualTo("Test Product");
		assertThat(responseBody.getQuantity()).isEqualTo(50);
	}

	@Test
	void test_UpdateProduct_ShouldUpdateEntity() {
		Long productId = createProductViaService("Original Product");

		UpdateProductRequestDTO updateRequest = new UpdateProductRequestDTO();
		updateRequest.setName("Updated Product Name");
		updateRequest.setDescription("Updated description");
		updateRequest.setQuantity(100); // Required field
		updateRequest.setLowStockThreshold(10); // Required field
		updateRequest.setProductTypeId(productType.getId());

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<UpdateProductRequestDTO> entity = new HttpEntity<>(updateRequest, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/api/v1/products/" + productId, HttpMethod.PATCH, entity,
				Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		Optional<Product> updatedProduct = productRepository.findById(productId);
		assertThat(updatedProduct).isPresent();
		assertThat(updatedProduct.get().getName()).isEqualTo("Updated Product Name");
		assertThat(updatedProduct.get().getDescription()).isEqualTo("Updated description");
	}

	@Test
	void test_UpdateQuantity_ShouldUpdateTheProductQuantityBasedOnOperation() {
		Long productId = createProductViaService("Quantity Test Product");

		UpdateQuantityRequestDTO quantityRequest = new UpdateQuantityRequestDTO();
		quantityRequest.setQuantity(25);
		quantityRequest.setOperation(OperationsType.INCREASE);

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<UpdateQuantityRequestDTO> entity = new HttpEntity<>(quantityRequest, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/api/v1/products/" + productId + "/quantity",
				HttpMethod.PATCH, entity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		Optional<Product> updatedProduct = productRepository.findById(productId);
		assertThat(updatedProduct).isPresent();
		assertThat(updatedProduct.get().getQuantity()).isEqualTo(75); // 50 + 25
	}

	@Test
	void test_DeleteProduct_ShouldSoftDeleteFromDatabase() {
		Long productId = createProductViaService("Delete Test Product");

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<Void> response = restTemplate.exchange("/api/v1/products/" + productId, HttpMethod.DELETE,
				entity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		Optional<Product> deletedProduct = productRepository.findById(productId);
		assertThat(deletedProduct).isEmpty();
	}

	@Test
	void test_GetProducts_ShouldReturnPagedResults() {
		createProductViaService("Product 1");
		createProductViaService("Product 2");

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/products?page=0&size=10", HttpMethod.GET,
				entity, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Product 1");
		assertThat(response.getBody()).contains("Product 2");
	}

	@Test
	void test_CreateProduct_WithInvalidData_ShouldReturnBadRequest() {
		CreateProductDTO request = new CreateProductDTO();
		request.setName(""); // invalid input: empty name
		request.setDescription("Test description");
		request.setQuantity(-5); // invalid input: negative quantity
		request.setLowStockThreshold(10);
		request.setProductTypeId(productType.getId());

		HttpHeaders headers = createAuthHeaders();
		HttpEntity<CreateProductDTO> entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/products", HttpMethod.POST, entity,
				String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void test_AccessProduct_WithoutAuthentication_ShouldReturnUnauthorized() {
		CreateProductDTO request = new CreateProductDTO();
		request.setName("Test Product");

		HttpEntity<CreateProductDTO> entity = new HttpEntity<>(request);

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/products", HttpMethod.POST, entity,
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

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(loginRequest, headers);

		ResponseEntity<JwtResponse> response = restTemplate.exchange("/api/v1/auth/login", HttpMethod.POST, entity,
				JwtResponse.class);

		if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
			return response.getBody().getToken();
		}
		throw new RuntimeException("Failed to authenticate test user");
	}

	private Long createProductViaService(String name) {
		CreateProductDTO request = new CreateProductDTO();
		request.setName(name);
		request.setDescription("Test description");
		request.setQuantity(50);
		request.setLowStockThreshold(10);
		request.setWeight(1.0);
		request.setHeight(5.0);
		request.setLength(10.0);
		request.setProductTypeId(productType.getId());

		return productService.save(request, testUserId);
	}
}
