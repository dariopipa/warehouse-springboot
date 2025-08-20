package io.github.dariopipa.warehouse.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;

public interface ProductService {

	Long save(CreateProductDTO product, Long loggedInUser);

	void update(Long id, UpdateProductRequestDTO product, Long loggedInUser);

	void delete(Long id, Long loggedInUser);

	Page<ProductGetOneResponseDTO> getCollection(Pageable pageable);

	ProductGetOneResponseDTO getById(Long id);

	Product getProductEntityById(Long id);

	void updateQuantity(Long id, UpdateQuantityRequestDTO updateQuantityRequestDTO, Long loggedInUser);
}
