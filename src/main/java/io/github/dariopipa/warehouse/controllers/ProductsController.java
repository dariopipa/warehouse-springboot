package io.github.dariopipa.warehouse.controllers;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.services.interfaces.ProductService;
import io.github.dariopipa.warehouse.utils.PaginationUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/products")
@Validated
@Tag(name = "Products")
public class ProductsController {

	private static final Logger logger = LoggerFactory
			.getLogger(ProductsController.class);
	private final ProductService productService;

	public ProductsController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("")
	public ResponseEntity<Void> createProduct(
			@AuthenticationPrincipal User loggedInUser,
			@Valid @RequestBody CreateProductDTO requestBody) {
		logger.info("Creating new product with name: {}",
				requestBody.getName());

		Long id = this.productService.save(requestBody, loggedInUser.getId());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(id).toUri();

		logger.info("Product created successfully with id: {}", id);
		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductGetOneResponseDTO> getProduct(
			@PathVariable Long id) {
		logger.info("Fetching product with id: {}", id);
		ProductGetOneResponseDTO productGetOneResponseDTO = this.productService
				.getById(id);

		logger.debug("Product retrieved successfully: {}",
				productGetOneResponseDTO.getName());
		return ResponseEntity.ok(productGetOneResponseDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id,
			@AuthenticationPrincipal User loggedInUser) {
		logger.info("Deleting product with id: {}", id);
		this.productService.delete(id, loggedInUser.getId());

		logger.info("Product deleted successfully with id: {}", id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateEntity(@PathVariable Long id,
			@Valid @RequestBody UpdateProductRequestDTO updateRequestDTO,
			@AuthenticationPrincipal User loggedInUser) {
		logger.info("Updating product with id: {} and name: {}", id,
				updateRequestDTO.getName());
		this.productService.update(id, updateRequestDTO, loggedInUser.getId());

		logger.info("Product updated successfully with id: {}", id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/quantity")
	public ResponseEntity<Void> updateProductQuantity(@PathVariable Long id,
			@Valid @RequestBody UpdateQuantityRequestDTO updateQuantityRequestDTO,
			@AuthenticationPrincipal User loggedInUser) {
		logger.info(
				"Updating quantity for product id: {} with operation: {} and quantity: {}",
				id, updateQuantityRequestDTO.getOperation(),
				updateQuantityRequestDTO.getQuantity());

		this.productService.updateQuantity(id, updateQuantityRequestDTO,
				loggedInUser.getId());
		logger.info("Product quantity updated successfully for id: {}", id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("")
	public PaginatedResponse<ProductGetOneResponseDTO> getProductCollection(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {
		logger.info(
				"Fetching product collection - page: {}, size: {}, sortBy: {}, direction: {}",
				page, size, sortBy, direction);

		List<String> allowed = List.of("name", "createdAt", "quantity");
		if (!allowed.contains(sortBy)) {
			logger.warn("Invalid sortBy parameter: {}. Allowed values: {}",
					sortBy, allowed);

			throw new IllegalArgumentException(
					"sortBy must be one of: " + allowed);
		}

		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<ProductGetOneResponseDTO> paginatedResponse = productService
				.getCollection(pageable);

		logger.debug(
				"Product collection retrieved successfully - total elements: {}",
				paginatedResponse.getTotalElements());

		return PaginationUtils.buildPaginatedResponse(paginatedResponse);
	}

}
