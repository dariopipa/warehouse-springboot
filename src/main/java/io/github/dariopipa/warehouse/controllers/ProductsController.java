package io.github.dariopipa.warehouse.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
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

	private final ProductService productService;

	public ProductsController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("")
	public ResponseEntity<Void> createProduct(
			@Valid @RequestBody CreateProductDTO requestBody) {

		Long id = this.productService.save(requestBody);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(id).toUri();

		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductGetOneResponseDTO> getProduct(
			@PathVariable Long id) {

		ProductGetOneResponseDTO productGetOneResponseDTO = this.productService
				.getById(id);
		return ResponseEntity.ok(productGetOneResponseDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

		this.productService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateEntity(@PathVariable Long id,
			@Valid @RequestBody UpdateProductRequestDTO updateRequestDTO) {

		this.productService.update(id, updateRequestDTO);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/quantity")
	public ResponseEntity<Void> updateProductQuantity(@PathVariable Long id,
			@Valid @RequestBody UpdateQuantityRequestDTO updateQuantityRequestDTO) {

		this.productService.updateQuantity(id, updateQuantityRequestDTO);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("")
	public PaginatedResponse<ProductGetOneResponseDTO> getProductCollection(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {

		List<String> allowed = List.of("name", "createdAt", "quantity");
		if (!allowed.contains(sortBy)) {
			throw new IllegalArgumentException(
					"sortBy must be one of: " + allowed);
		}

		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<ProductGetOneResponseDTO> paginatedResponse = productService
				.getCollection(pageable);

		return PaginationUtils.buildPaginatedResponse(paginatedResponse);
	}

}
