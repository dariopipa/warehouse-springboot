package io.github.dariopipa.warehouse.services.interfaces;

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import java.util.List;

public interface ProductService {

	Long save(CreateProductDTO product);

	void update(Long id, UpdateRequestDTO product);

	void delete(Long id);

	List<ProductGetOneResponseDTO> getCollection();

	ProductGetOneResponseDTO getById(Long id);

	void updateQuantity(Long id,
			UpdateQuantityRequestDTO updateQuantityRequestDTO);
}
