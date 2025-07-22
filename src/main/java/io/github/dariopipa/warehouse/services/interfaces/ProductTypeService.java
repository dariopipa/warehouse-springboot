package io.github.dariopipa.warehouse.services.interfaces;

import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductTypeService {

	Long save(ProductTypesDTO productType);

	void update(Long id, ProductTypesDTO productType);

	void delete(Long id);

	Page<ProductTypeResponseDTO> getCollection(Pageable pageable);

	ProductTypeResponseDTO getById(Long id);

	ProductType getProductType(Long id);
}
