package io.github.dariopipa.warehouse.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.enums.OperationsType;
import io.github.dariopipa.warehouse.enums.ProductSortByEnum;
import io.github.dariopipa.warehouse.enums.SortDirectionEnum;
import io.github.dariopipa.warehouse.exceptions.ConflictException;
import io.github.dariopipa.warehouse.exceptions.EntityNotFoundException;
import io.github.dariopipa.warehouse.services.interfaces.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

	@Mock
	private ProductService productService;

	@InjectMocks
	private ProductsController productsController;

	private User loggedInUser;
	private CreateProductDTO createProductDTO;
	private UpdateProductRequestDTO updateProductRequestDTO;
	private UpdateQuantityRequestDTO updateQuantityRequestDTO;
	private ProductGetOneResponseDTO productResponseDTO;
	private Long productId;

	@BeforeEach
	void setUp() {
		productId = 1L;

		loggedInUser = new User();
		loggedInUser.setId(1L);

		createProductDTO = new CreateProductDTO();
		createProductDTO.setName("Test Product");
		createProductDTO.setDescription("Test Description");
		createProductDTO.setQuantity(100);
		createProductDTO.setLowStockThreshold(10);
		createProductDTO.setWeight(1.5);
		createProductDTO.setHeight(10.0);
		createProductDTO.setLength(20.0);
		createProductDTO.setProductTypeId(1L);

		updateProductRequestDTO = new UpdateProductRequestDTO();
		updateProductRequestDTO.setName("Updated Product");
		updateProductRequestDTO.setDescription("Updated Description");

		updateQuantityRequestDTO = new UpdateQuantityRequestDTO();
		updateQuantityRequestDTO.setQuantity(50);
		updateQuantityRequestDTO.setOperation(OperationsType.INCREASE);

		productResponseDTO = new ProductGetOneResponseDTO();
		productResponseDTO.setId(productId);
		productResponseDTO.setName("Test Product");
		productResponseDTO.setDescription("Test Description");
		productResponseDTO.setQuantity(100);
	}

	@Test
	void test_CreateProduct_ShouldCallService_DTOIsValid() {
		when(productService.save(createProductDTO, loggedInUser.getId())).thenReturn(productId);

		assertThrows(IllegalStateException.class, () -> {
			productsController.createProduct(loggedInUser, createProductDTO);
		});

		verify(productService).save(createProductDTO, loggedInUser.getId());
	}

	@Test
	void test_CreateProduct_ShouldThrowException_WhenProductAlreadyExists() {
		createProductDTO.setName("Existing Product");

		when(productService.save(createProductDTO, loggedInUser.getId()))
				.thenThrow(new ConflictException("Product already exists"));

		assertThrows(ConflictException.class, () -> {
			productsController.createProduct(loggedInUser, createProductDTO);
		});

		verify(productService).save(createProductDTO, loggedInUser.getId());
	}

	@Test
	void test_GetProduct_ShouldReturnProduct_WhenProductExists() {
		when(productService.getById(productId)).thenReturn(productResponseDTO);

		ResponseEntity<ProductGetOneResponseDTO> response = productsController.getProduct(productId);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(productResponseDTO, response.getBody());
		verify(productService).getById(productId);
	}

	@Test
	void test_GetProduct_ShouldThrowException_WhenProductDoesNotExist() {
		Long nonExistentProductId = 999L;

		when(productService.getById(nonExistentProductId)).thenThrow(new EntityNotFoundException("Product not found"));

		assertThrows(EntityNotFoundException.class, () -> {
			productsController.getProduct(nonExistentProductId);
		});

		verify(productService).getById(nonExistentProductId);
	}

	@Test
	void test_GetProductCollection_ShouldReturnPaginatedResponse_WithDefaultParameters() {
		List<ProductGetOneResponseDTO> products = Arrays.asList(createProductResponseDTO(1L, "Product 1"),
				createProductResponseDTO(2L, "Product 2"));

		Page<ProductGetOneResponseDTO> page = new PageImpl<>(products,
				PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name")), 2);

		when(productService.getCollection(any(Pageable.class))).thenReturn(page);

		PaginatedResponse<ProductGetOneResponseDTO> response = productsController.getProductCollection(0, 10,
				ProductSortByEnum.name, SortDirectionEnum.asc);

		assertNotNull(response);
		verify(productService).getCollection(any(Pageable.class));
	}

	@Test
	void test_DeleteProduct_ShouldReturnNoContent_WhenProductExists() {
		doNothing().when(productService).delete(productId, loggedInUser.getId());

		ResponseEntity<Void> response = productsController.deleteProduct(productId, loggedInUser);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(productService).delete(productId, loggedInUser.getId());
	}

	@Test
	void test_DeleteProduct_ShouldThrowException_WhenProductDoesNotExist() {
		Long nonExistentProductId = 999L;

		doThrow(new EntityNotFoundException("Product not found")).when(productService).delete(nonExistentProductId,
				loggedInUser.getId());

		assertThrows(EntityNotFoundException.class, () -> {
			productsController.deleteProduct(nonExistentProductId, loggedInUser);
		});

		verify(productService).delete(nonExistentProductId, loggedInUser.getId());
	}

	@Test
	void test_UpdateProduct_ShouldReturnNoContent_WhenCreatingNewProduct() {
		doNothing().when(productService).update(productId, updateProductRequestDTO, loggedInUser.getId());

		ResponseEntity<Void> response = productsController.updateEntity(productId, updateProductRequestDTO,
				loggedInUser);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(productService).update(productId, updateProductRequestDTO, loggedInUser.getId());
	}

	@Test
	void test_UpdateProductQuantity_ShouldReturnNoContent_WhenCreationFails() {
		doNothing().when(productService).updateQuantity(productId, updateQuantityRequestDTO, loggedInUser.getId());

		ResponseEntity<Void> response = productsController.updateProductQuantity(productId, updateQuantityRequestDTO,
				loggedInUser);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(productService).updateQuantity(productId, updateQuantityRequestDTO, loggedInUser.getId());
	}

	private ProductGetOneResponseDTO createProductResponseDTO(Long id, String name) {
		ProductGetOneResponseDTO dto = new ProductGetOneResponseDTO();
		dto.setId(id);
		dto.setName(name);
		dto.setDescription("Description for " + name);
		dto.setQuantity(100);
		return dto;
	}
}
