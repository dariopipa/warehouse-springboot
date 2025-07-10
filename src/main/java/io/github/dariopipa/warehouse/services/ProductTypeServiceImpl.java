package io.github.dariopipa.warehouse.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.mappers.ProductTypeMapper;
import io.github.dariopipa.warehouse.repositories.ProductTypeRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;


@Service
public class ProductTypeServiceImpl implements  ProductTypeService{

	// REMOVE THE HARD-CODED USER WHEN AUTHENTICATION IS IMPLEMENTED.
	private final Long USER_ID = 1L;

	private final Logger log = LoggerFactory.getLogger(ProductTypeServiceImpl.class);
	private final ProductTypeRepository productTypeRepository;

	public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository) {
		this.productTypeRepository = productTypeRepository;
	}

	@Override
	public List<ProductType> getCollection() {
		List<ProductType> productTypes = productTypeRepository.findAll();
		return productTypes;
	}

	@Override
	public ProductTypeResponseDTO getById(Long id) {

		ProductType productType = productTypeRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product type not found"));

		return ProductTypeMapper.toDto(productType);
	}


	@Override
	public Long save(ProductTypesDTO dto) {

		Boolean exists = productTypeRepository.existsByName(dto.getName());
		if (exists) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Product type name already exists");
		}

		ProductType entity = ProductTypeMapper.toEntity(dto, USER_ID);
		ProductType saved = productTypeRepository.save(entity);

		return saved.getId();
	}

	@Override
	public void update(Long id, ProductTypesDTO productType) {

		ProductType existing = this.productTypeRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product type does not exist"));

		boolean nameExists = this.productTypeRepository.existsByNameAndIdNot(productType.getName(), id);
		if (nameExists) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Product type name already exists");
		}


		existing.setName(productType.getName());
		this.productTypeRepository.save(existing);
	}

	@Override
	public void delete(Long id) {
		ProductType productType = productTypeRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product type not found"));

		productTypeRepository.delete(productType);
	}

}
