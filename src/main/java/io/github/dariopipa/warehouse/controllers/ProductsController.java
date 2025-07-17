package io.github.dariopipa.warehouse.controllers;

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.services.interfaces.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.net.URI;
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
@RequestMapping("/api/v1/products")
@Tag(name = "Products")
public class ProductsController {

    private final ProductService productService;

    public ProductsController(ProductService productService) {
	this.productService = productService;
    }

    @PostMapping("")
    public ResponseEntity<Void> createProduct(@Valid @RequestBody CreateProductDTO requestBody) {

	Long id = this.productService.save(requestBody);
	URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

	return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductGetOneResponseDTO> getProduct(@PathVariable Long id) {

	ProductGetOneResponseDTO productGetOneResponseDTO = this.productService.getById(id);
	return ResponseEntity.ok(productGetOneResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

	this.productService.delete(id);
	return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateEntity(@PathVariable Long id,
	    @Valid @RequestBody UpdateRequestDTO updateRequestDTO) {

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
    public List<ProductGetOneResponseDTO> getProductCollection() {

	return this.productService.getCollection();
    }

}
