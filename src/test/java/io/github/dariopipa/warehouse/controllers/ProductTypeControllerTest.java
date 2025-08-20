package io.github.dariopipa.warehouse.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
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

import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.enums.ProductTypeSortByEnum;
import io.github.dariopipa.warehouse.enums.SortDirectionEnum;
import io.github.dariopipa.warehouse.exceptions.ConflictException;
import io.github.dariopipa.warehouse.exceptions.EntityNotFoundException;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;

@ExtendWith(MockitoExtension.class)
class ProductTypeControllerTest {

	@Mock
	private ProductTypeService productTypeService;

	@InjectMocks
	private ProductTypeController productTypeController;

	private User loggedInUser;
	private ProductTypesDTO productTypesDTO;
	private ProductTypeResponseDTO productTypeResponseDTO;
	private Long productTypeId;

	@BeforeEach
	void setUp() {
		productTypeId = 1L;

		loggedInUser = new User();
		loggedInUser.setId(1L);

		productTypesDTO = new ProductTypesDTO();
		productTypesDTO.setName("Electronics");

		productTypeResponseDTO = new ProductTypeResponseDTO();
		productTypeResponseDTO.setId(productTypeId);
		productTypeResponseDTO.setName("Electronics");
		productTypeResponseDTO.setCreatedAt(Instant.now());
		productTypeResponseDTO.setUpdatedAt(Instant.now());
		productTypeResponseDTO.setCreatedBy(1L);
		productTypeResponseDTO.setUpdatedBy(1L);
	}

	@Test
	void test_CreateProductTypes_ShouldCallService_WhenRequestDtoIsValid() {
		when(productTypeService.save(productTypesDTO, loggedInUser.getId())).thenReturn(productTypeId);

		assertThrows(IllegalStateException.class, () -> {
			productTypeController.createProductTypes(productTypesDTO, loggedInUser);
		});

		verify(productTypeService).save(productTypesDTO, loggedInUser.getId());
	}

	@Test
	void test_CreateProductTypes_ShouldThrowException_ProductTypeAlreadyExists() {
		productTypesDTO.setName("Existing Type");

		when(productTypeService.save(productTypesDTO, loggedInUser.getId()))
				.thenThrow(new ConflictException("Product type already exists"));

		assertThrows(ConflictException.class, () -> {
			productTypeController.createProductTypes(productTypesDTO, loggedInUser);
		});

		verify(productTypeService).save(productTypesDTO, loggedInUser.getId());
	}

	@Test
	void test_GetProductType_ShouldReturnProductType_WhenProductTypeExists() {
		when(productTypeService.getById(productTypeId)).thenReturn(productTypeResponseDTO);

		ResponseEntity<ProductTypeResponseDTO> response = productTypeController.getProductType(productTypeId);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(productTypeResponseDTO, response.getBody());
		verify(productTypeService).getById(productTypeId);
	}

	@Test
	void test_GetProductType_ShouldThrowException_WhenProductTypeDoesNotExist() {
		Long nonExistentProductTypeId = 999L;

		when(productTypeService.getById(nonExistentProductTypeId))
				.thenThrow(new EntityNotFoundException("Product type not found"));

		assertThrows(EntityNotFoundException.class, () -> {
			productTypeController.getProductType(nonExistentProductTypeId);
		});

		verify(productTypeService).getById(nonExistentProductTypeId);
	}

	@Test
	void test_GetProductTypes_ShouldReturnPaginatedResponse_WithDefaultParameters() {
		List<ProductTypeResponseDTO> productTypes = Arrays.asList(createProductTypeResponseDTO(1L, "Electronics"),
				createProductTypeResponseDTO(2L, "Clothing"));

		Page<ProductTypeResponseDTO> page = new PageImpl<>(productTypes,
				PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name")), 2);

		when(productTypeService.getCollection(any(Pageable.class))).thenReturn(page);

		PaginatedResponse<ProductTypeResponseDTO> response = productTypeController.getProductTypes(0, 10,
				ProductTypeSortByEnum.name, SortDirectionEnum.desc);

		assertNotNull(response);
		verify(productTypeService).getCollection(any(Pageable.class));
	}

	@Test
	void test_DeleteProductTypes_ShouldReturnNoContent_WhenProductTypeExists() {
		doNothing().when(productTypeService).delete(productTypeId, loggedInUser.getId());

		ResponseEntity<Void> response = productTypeController.deleteProductTypes(productTypeId, loggedInUser);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(productTypeService).delete(productTypeId, loggedInUser.getId());
	}

	@Test
	void test_DeleteProductTypes_ShouldThrowException_WhenProductTypeDoesNotExist() {
		Long nonExistentProductTypeId = 999L;

		doThrow(new EntityNotFoundException("Product type not found")).when(productTypeService)
				.delete(nonExistentProductTypeId, loggedInUser.getId());

		assertThrows(EntityNotFoundException.class, () -> {
			productTypeController.deleteProductTypes(nonExistentProductTypeId, loggedInUser);
		});

		verify(productTypeService).delete(nonExistentProductTypeId, loggedInUser.getId());
	}

	@Test
	void test_UpdateProductTypes_ShouldReturnNoContent_WhenRequestDTOIsValid() {
		ProductTypesDTO updateDTO = new ProductTypesDTO();
		updateDTO.setName("Updated Electronics");

		doNothing().when(productTypeService).update(productTypeId, updateDTO, loggedInUser.getId());

		ResponseEntity<ProductType> response = productTypeController.updateProductTypes(productTypeId, updateDTO,
				loggedInUser);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(productTypeService).update(productTypeId, updateDTO, loggedInUser.getId());
	}

	private ProductTypeResponseDTO createProductTypeResponseDTO(Long id, String name) {
		ProductTypeResponseDTO dto = new ProductTypeResponseDTO();
		dto.setId(id);
		dto.setName(name);
		dto.setCreatedAt(Instant.now());
		dto.setUpdatedAt(Instant.now());
		dto.setCreatedBy(1L);
		dto.setUpdatedBy(1L);
		return dto;
	}
}
