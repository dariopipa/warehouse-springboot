package io.github.dariopipa.warehouse.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.audit.AuditLogger;
import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.exceptions.ConflictException;
import io.github.dariopipa.warehouse.exceptions.EntityNotFoundException;
import io.github.dariopipa.warehouse.mappers.ProductTypeMapper;
import io.github.dariopipa.warehouse.repositories.ProductTypeRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {

	private final Logger logger = LoggerFactory.getLogger(ProductTypeServiceImpl.class);
	private final ProductTypeRepository productTypeRepository;
	private final AuditLogger auditLogger;

	public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository, AuditLogger auditLogger) {
		this.productTypeRepository = productTypeRepository;
		this.auditLogger = auditLogger;
	}

	@Override
	public Page<ProductTypeResponseDTO> getCollection(Pageable pageable) {
		logger.info("Fetching product type collection with pageable: {}", pageable);

		return productTypeRepository.findAll(pageable).map(ProductTypeMapper::toDto);
	}

	@Override
	public ProductTypeResponseDTO getById(Long id) {
		logger.info("Fetching product type by id: {}", id);

		ProductType productType = getProductType(id);
		return ProductTypeMapper.toDto(productType);
	}

	@Override
	public Long save(ProductTypesDTO dto, Long loggedInUser) {
		logger.info("Saving new product type: {}", dto.getName());

		if (productTypeRepository.existsByName(dto.getName())) {
			logger.warn("Product type name already exists: {}", dto.getName());
			throw new ConflictException("Product type name already exists");
		}

		ProductType entity = ProductTypeMapper.toEntity(dto, loggedInUser);
		ProductType saved = productTypeRepository.save(entity);

		logger.info("Product type saved with id: {}", saved.getId());

		auditLogger.log(loggedInUser, AuditAction.CREATE, EntityType.PRODUCT_TYPE, saved.getId());
		return saved.getId();
	}

	@Override
	public void update(Long id, ProductTypesDTO productType, Long loggedInUser) {
		logger.info("Updating product type with id: {} and name: {}", id, productType.getName());

		ProductType existingProductType = getProductType(id);
		if (this.productTypeRepository.existsByNameAndIdNot(productType.getName(), id)) {
			logger.warn("Product type name already exists for update: {}", productType.getName());
			throw new ConflictException("Product type name already exists");
		}

		existingProductType.setName(productType.getName());
		this.productTypeRepository.save(existingProductType);
		logger.info("Product type updated with id: {}", id);

		auditLogger.log(loggedInUser, AuditAction.UPDATE, EntityType.PRODUCT_TYPE, id);
	}

	@Override
	public void delete(Long id, Long loggedInUser) {
		logger.info("Deleting product type with id: {}", id);

		ProductType productType = getProductType(id);
		this.productTypeRepository.delete(productType);
		logger.info("Product type deleted with id: {}", id);

		auditLogger.log(loggedInUser, AuditAction.DELETE, EntityType.PRODUCT_TYPE, id);
	}

	@Override
	public ProductType getProductType(Long id) {
		logger.debug("Retrieving product type with id: {}", id);

		return productTypeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product type not found with id: " + id));
	}
}
