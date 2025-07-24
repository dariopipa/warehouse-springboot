package io.github.dariopipa.warehouse.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.audit.AuditAction;
import io.github.dariopipa.warehouse.audit.AuditLogger;
import io.github.dariopipa.warehouse.audit.EntityType;
import io.github.dariopipa.warehouse.controllers.ProductsController;
import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.exceptions.ConflictException;
import io.github.dariopipa.warehouse.exceptions.EntityNotFoundException;
import io.github.dariopipa.warehouse.mappers.ProductMapper;
import io.github.dariopipa.warehouse.repositories.ProductRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductService;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;
import io.github.dariopipa.warehouse.services.interfaces.SkuGeneratorService;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	private final Logger logger = LoggerFactory
			.getLogger(ProductServiceImpl.class);

	private final Long USER_ID = 1L;
	private final ProductRepository productRepository;
	private final ProductTypeService productTypeService;
	private final SkuGeneratorService skuGeneratorService;
	private final AuditLogger auditLogger;

	public ProductServiceImpl(ProductRepository productRepository,
			ProductTypeService productTypeService,
			SkuGeneratorService skuGeneratorService, AuditLogger auditLogger) {
		this.productRepository = productRepository;
		this.productTypeService = productTypeService;
		this.skuGeneratorService = skuGeneratorService;
		this.auditLogger = auditLogger;
	}

	@Override
	public Long save(CreateProductDTO dto) {

		logger.info("Saving new product: {}", dto.getName());
		if (this.productRepository.existsByName(dto.getName())) {
			logger.warn("Product already exists with name: {}", dto.getName());
			throw new ConflictException(
					"Product already exists with that name");
		}

		ProductType productType = productTypeService
				.getProductType(dto.getProductTypeId());
		String generatedSku = skuGeneratorService.generateSku(dto.getName(),
				productType.getName());
		Product productEntity = ProductMapper.toEntity(dto, USER_ID,
				productType, generatedSku);
		try {
			Product savedEntity = this.productRepository.save(productEntity);
			logger.info("Product saved with id: {}", savedEntity.getId());

			auditLogger.log(USER_ID, AuditAction.CREATE,
					EntityType.PRODUCT, savedEntity.getId());

			return savedEntity.getId();
		} catch (DataIntegrityViolationException e) {
			logger.error("Data integrity violation while saving product: {}",
					e.getMessage());

			throw new ConflictException("Sku already exists");
		}

	}

	@Override
	public void update(Long id, UpdateProductRequestDTO updateRequestDTO) {
		logger.info("Updating product with id: {}", id);

		Product product = getProduct(id);
		ProductType productType = productTypeService
				.getProductType(updateRequestDTO.getProductTypeId());

		this.productRepository.save(ProductMapper
				.updateEntityFromDto(updateRequestDTO, product, productType));
		logger.info("Product updated with id: {}", id);
		
		auditLogger.log(USER_ID, AuditAction.UPDATE,
				EntityType.PRODUCT, id);
	}

	@Override
	public void delete(Long id) {
		logger.info("Deleting product with id: {}", id);
		Product product = getProduct(id);

		this.productRepository.delete(product);
		logger.info("Product deleted with id: {}", id);
		auditLogger.log(USER_ID, AuditAction.DELETE,
				EntityType.PRODUCT, id);
	}

	@Override
	public Page<ProductGetOneResponseDTO> getCollection(Pageable pageable) {
		logger.info("Fetching product collection with pageable: {}", pageable);
		return productRepository.findAll(pageable).map(ProductMapper::toDto);
	}

	@Override
	public ProductGetOneResponseDTO getById(final Long id) {
		logger.info("Fetching product by id: {}", id);

		Product product = getProduct(id);
		return ProductMapper.toDto(product);
	}

	// AFTER FIXING THE GAZILLION MERGE CONFLICTS ADD HERE THE AUDITLOGGER.LOG();
	@Override
	@Transactional
	public void updateQuantity(Long id,
			UpdateQuantityRequestDTO updateQuantityRequestDTO) {

		logger.info(
				"Updating quantity for product id: {} with operation: {} and quantity: {}",
				id, updateQuantityRequestDTO.getOperation(),
				updateQuantityRequestDTO.getQuantity());

		getProduct(id);
		int delta = updateQuantityRequestDTO
				.getOperation() == UpdateQuantityRequestDTO.Operation.INCREASE
						? updateQuantityRequestDTO.getQuantity()
						: -updateQuantityRequestDTO.getQuantity();

		this.productRepository.updateQuantityById(id, delta);

		logger.info("Quantity updated for product id: {} by delta: {}", id,
				delta);
	}

	private Product getProduct(Long id) {
		logger.debug("Retrieving product with id: {}", id);

		return this.productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"Product not found with id: " + id));
	}

}
