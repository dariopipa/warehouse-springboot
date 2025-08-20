package io.github.dariopipa.warehouse.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.audit.AuditLogger;
import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.enums.OperationsType;
import io.github.dariopipa.warehouse.exceptions.ConflictException;
import io.github.dariopipa.warehouse.exceptions.EntityNotFoundException;
import io.github.dariopipa.warehouse.mappers.ProductMapper;
import io.github.dariopipa.warehouse.repositories.ProductRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductService;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;
import io.github.dariopipa.warehouse.services.interfaces.SkuGeneratorService;
import io.github.dariopipa.warehouse.services.interfaces.StockAlertService;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

	private final ProductRepository productRepository;
	private final ProductTypeService productTypeService;
	private final SkuGeneratorService skuGeneratorService;
	private final AuditLogger auditLogger;
	private final StockAlertService stockAlertService;

	public ProductServiceImpl(ProductRepository productRepository, ProductTypeService productTypeService,
			SkuGeneratorService skuGeneratorService, AuditLogger auditLogger, StockAlertService stockAlertService) {
		this.productRepository = productRepository;
		this.productTypeService = productTypeService;
		this.skuGeneratorService = skuGeneratorService;
		this.auditLogger = auditLogger;
		this.stockAlertService = stockAlertService;

	}

	@Override
	public Long save(CreateProductDTO dto, Long loggedInUser) {

		logger.info("Saving new product: {}", dto.getName());
		if (this.productRepository.existsByName(dto.getName())) {
			logger.warn("Product already exists with name: {}", dto.getName());
			throw new ConflictException("Product already exists with that name");
		}

		ProductType productType = productTypeService.getProductType(dto.getProductTypeId());
		String generatedSku = skuGeneratorService.generateSku(dto.getName(), productType.getName());
		Product productEntity = ProductMapper.toEntity(dto, loggedInUser, productType, generatedSku);
		try {
			Product savedEntity = this.productRepository.save(productEntity);
			logger.info("Product saved with id: {}", savedEntity.getId());

			auditLogger.log(loggedInUser, AuditAction.CREATE, EntityType.PRODUCT, savedEntity.getId());

			return savedEntity.getId();
		} catch (DataIntegrityViolationException e) {
			logger.error("Data integrity violation while saving product: {}", e.getMessage());

			throw new ConflictException("Sku already exists");
		}

	}

	@Override
	public void update(Long id, UpdateProductRequestDTO updateRequestDTO, Long loggedInUser) {
		logger.info("Updating product with id: {}", id);

		Product product = getProduct(id);
		ProductType productType = productTypeService.getProductType(updateRequestDTO.getProductTypeId());

		this.productRepository.save(ProductMapper.updateEntityFromDto(updateRequestDTO, product, productType));
		logger.info("Product updated with id: {}", id);

		auditLogger.log(loggedInUser, AuditAction.UPDATE, EntityType.PRODUCT, id);
	}

	@Override
	public void delete(Long id, Long loggedInUser) {
		logger.info("Deleting product with id: {}", id);
		Product product = getProduct(id);

		this.productRepository.delete(product);
		logger.info("Product deleted with id: {}", id);
		auditLogger.log(loggedInUser, AuditAction.DELETE, EntityType.PRODUCT, id);
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

	@Override
	@Transactional
	public void updateQuantity(Long id, UpdateQuantityRequestDTO updateQuantityRequestDTO, Long loggedInUser) {

		logger.info("Updating quantity for product id: {} with operation: {} and quantity: {}", id,
				updateQuantityRequestDTO.getOperation(), updateQuantityRequestDTO.getQuantity());

		Product product = getProduct(id);
		int delta = updateQuantityRequestDTO.getOperation() == OperationsType.INCREASE
				? updateQuantityRequestDTO.getQuantity()
				: -updateQuantityRequestDTO.getQuantity();

		int newQuantity = product.getQuantity() + delta;
		if (newQuantity < 0) {
			throw new IllegalArgumentException("Product quantity cannot be reduced to less than 0");
		}

		this.productRepository.updateQuantityById(id, delta);
		this.stockAlertService.alertStockLow(product, newQuantity);
		this.auditLogger.logQuantityUpdate(loggedInUser, EntityType.PRODUCT, id,
				updateQuantityRequestDTO.getOperation(), newQuantity);

		logger.info("Quantity updated for product id: {} by delta: {}", id, delta);
	}

	private Product getProduct(Long id) {
		logger.debug("Retrieving product with id: {}", id);

		return this.productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
	}

	@Override
	public Product getProductEntityById(Long id) {
		return this.productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
	}

}
