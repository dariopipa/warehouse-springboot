package io.github.dariopipa.warehouse.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.exceptions.ConflictException;
import io.github.dariopipa.warehouse.exceptions.EntityNotFoundException;
import io.github.dariopipa.warehouse.mappers.ProductTypeMapper;
import io.github.dariopipa.warehouse.repositories.ProductTypeRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {

	// REMOVE THE HARD-CODED USER WHEN AUTHENTICATION IS IMPLEMENTED.
	private final Long USER_ID = 1L;

	private final Logger log = LoggerFactory
			.getLogger(ProductTypeServiceImpl.class);
	private final ProductTypeRepository productTypeRepository;

	public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository) {
		this.productTypeRepository = productTypeRepository;
	}

	@Override
	public Page<ProductTypeResponseDTO> getCollection(Pageable pageable) {
		return productTypeRepository.findAll(pageable)
				.map(ProductTypeMapper::toDto);
	}

	@Override
	public ProductTypeResponseDTO getById(Long id) {

		ProductType productType = getProductType(id);
		return ProductTypeMapper.toDto(productType);
	}

	@Override
	public Long save(ProductTypesDTO dto) {

		if (productTypeRepository.existsByName(dto.getName())) {
			throw new ConflictException("Product type name already exists");
		}

		ProductType entity = ProductTypeMapper.toEntity(dto, USER_ID);
		ProductType saved = productTypeRepository.save(entity);

		return saved.getId();
	}

	@Override
	public void update(Long id, ProductTypesDTO productType) {

		ProductType existingProductType = getProductType(id);
		if (this.productTypeRepository
				.existsByNameAndIdNot(productType.getName(), id)) {
			throw new ConflictException("Product type name already exists");
		}

		existingProductType.setName(productType.getName());
		this.productTypeRepository.save(existingProductType);
	}

	@Override
	public void delete(Long id) {

		ProductType productType = getProductType(id);
		this.productTypeRepository.delete(productType);
	}

	public ProductType getProductType(Long id) {
		return productTypeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"Product type not found with id: " + id));
	}
}
