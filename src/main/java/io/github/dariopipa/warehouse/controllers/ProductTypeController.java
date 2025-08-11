package io.github.dariopipa.warehouse.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;

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

import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.entities.User;
import io.github.dariopipa.warehouse.enums.ProductTypeSortByEnum;
import io.github.dariopipa.warehouse.enums.SortDirectionEnum;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;
import io.github.dariopipa.warehouse.utils.PaginationUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/product-types")
@Validated
@Tag(name = "Products Type")
public class ProductTypeController {

	private final Logger logger = LoggerFactory
			.getLogger(ProductTypeController.class);

	private final ProductTypeService productTypeService;

	public ProductTypeController(ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}

	@GetMapping
	public PaginatedResponse<ProductTypeResponseDTO> getProductTypes(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(defaultValue = "name") ProductTypeSortByEnum sortBy,
			@RequestParam(defaultValue = "desc") SortDirectionEnum direction) {
		logger.info(
				"Fetching product types collection - page: {}, size: {}, sortBy: {}, direction: {}",
				page, size, sortBy, direction);

		String sortColumn = sortBy.getProperty();
		Sort.Direction sortDirection = Sort.Direction
				.fromString(direction.name());

		Sort sort = Sort.by(sortDirection, sortColumn);
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<ProductTypeResponseDTO> paginatedResponse = productTypeService
				.getCollection(pageable);

		paginatedResponse.forEach(p -> p.add(
				linkTo(methodOn(ProductTypeController.class)
						.getProductType(p.getId())).withSelfRel(),

				linkTo(methodOn(ProductTypeController.class)
						.updateProductTypes(p.getId(), null, null))
						.withRel("update"),

				linkTo(methodOn(ProductTypeController.class)
						.deleteProductTypes(p.getId(), null))
						.withRel("delete")));

		logger.debug(
				"Product types collection retrieved successfully - total elements: {}",
				paginatedResponse.getTotalElements());

		return PaginationUtils.buildPaginatedResponse(paginatedResponse);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductTypeResponseDTO> getProductType(
			@PathVariable Long id) {
		logger.info("Fetching product type with id: {}", id);

		ProductTypeResponseDTO productType = this.productTypeService
				.getById(id);

		productType.add(

				linkTo(methodOn(ProductTypeController.class).getProductType(id))
						.withSelfRel(),

				linkTo(methodOn(ProductTypeController.class).getProductTypes(0,
						10, ProductTypeSortByEnum.name, SortDirectionEnum.asc))
						.withRel("collection"),

				linkTo(methodOn(ProductTypeController.class)
						.updateProductTypes(id, null, null)).withRel("update"),

				linkTo(methodOn(ProductTypeController.class)
						.deleteProductTypes(id, null)).withRel("delete"));

		logger.debug("Product type retrieved successfully: {}",
				productType.getName());

		return ResponseEntity.ok(productType);
	}

	@PostMapping("")
	public ResponseEntity<Void> createProductTypes(
			@Valid @RequestBody ProductTypesDTO productType,
			@AuthenticationPrincipal User loggedInUser) {
		logger.info("Creating new product type with name: {}",
				productType.getName());

		Long id = this.productTypeService.save(productType,
				loggedInUser.getId());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(id).toUri();

		logger.info("Product type created successfully with id: {}", id);
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProductTypes(@PathVariable Long id,
			@AuthenticationPrincipal User loggedInUser) {
		logger.info("Deleting product type with id: {}", id);
		this.productTypeService.delete(id, loggedInUser.getId());

		logger.info("Product type deleted successfully with id: {}", id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ProductType> updateProductTypes(@PathVariable Long id,
			@Valid @RequestBody ProductTypesDTO productType,
			@AuthenticationPrincipal User loggedInUser) {
		logger.info("Updating product type with id: {} and name: {}", id,
				productType.getName());
		this.productTypeService.update(id, productType, loggedInUser.getId());

		logger.info("Product type updated successfully with id: {}", id);
		return ResponseEntity.noContent().build();
	}
}
