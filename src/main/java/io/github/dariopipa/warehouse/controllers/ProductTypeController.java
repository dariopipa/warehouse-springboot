package io.github.dariopipa.warehouse.controllers;

import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/product-types")
@Tag(name = "Products Type")
public class ProductTypeController {

    private final ProductTypeService productTypeService;

    public ProductTypeController(ProductTypeService productTypeService) {
	this.productTypeService = productTypeService;
    }

    @GetMapping("")
    public List<ProductType> getProductTypes() {
	return this.productTypeService.getCollection();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductTypeResponseDTO> getProductType(@PathVariable Long id) {
	ProductTypeResponseDTO productType = this.productTypeService.getById(id);
	return ResponseEntity.ok(productType);
    }

    @PostMapping("")
    public ResponseEntity<Void> createProductTypes(@Valid @RequestBody ProductTypesDTO productType) {
	Long id = this.productTypeService.save(productType);

	URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

	return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductTypes(@PathVariable Long id) {
	this.productTypeService.delete(id);
	return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductType> updateProductTypes(@PathVariable Long id,
	    @Valid @RequestBody ProductTypesDTO productType) {

	this.productTypeService.update(id, productType);
	return ResponseEntity.noContent().build();
    }
}
