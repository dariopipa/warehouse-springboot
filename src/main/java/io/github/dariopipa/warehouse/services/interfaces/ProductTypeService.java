package io.github.dariopipa.warehouse.services.interfaces;

import java.util.List;

import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;

public interface ProductTypeService {
	Long save(ProductTypesDTO productType);
	void update(Long id, ProductTypesDTO productType);
	void delete(Long id);
	List<ProductType> getCollection();
	ProductTypeResponseDTO getById(Long id);
}
