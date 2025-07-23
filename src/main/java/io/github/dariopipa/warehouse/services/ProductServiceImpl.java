package io.github.dariopipa.warehouse.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

	private final Long id = 1L;

	private final ProductRepository productRepository;
	private final ProductTypeService productTypeService;
	private final SkuGeneratorService skuGeneratorService;

	public ProductServiceImpl(ProductRepository productRepository,
			ProductTypeService productTypeService,
			SkuGeneratorService skuGeneratorService) {
		this.productRepository = productRepository;
		this.productTypeService = productTypeService;
		this.skuGeneratorService = skuGeneratorService;
	}

	@Override
	public Long save(CreateProductDTO dto) {

		if (this.productRepository.existsByName(dto.getName())) {
			throw new ConflictException(
					"Product already exists with that name");
		}

		ProductType productType = productTypeService
				.getProductType(dto.getProductTypeId());

		String generatedSku = skuGeneratorService.generateSku(dto.getName(),
				productType.getName());

		Product productEntity = ProductMapper.toEntity(dto, id, productType,
				generatedSku);
		try {
			Product savedEntity = this.productRepository.save(productEntity);
			return savedEntity.getId();

		} catch (DataIntegrityViolationException e) {
			throw new ConflictException("Sku already exists");
		}

	}

	@Override
	public void update(Long id, UpdateProductRequestDTO updateRequestDTO) {

		Product product = getProduct(id);
		ProductType productType = productTypeService
				.getProductType(updateRequestDTO.getProductTypeId());

		this.productRepository.save(ProductMapper
				.updateEntityFromDto(updateRequestDTO, product, productType));
	}

	@Override
	public void delete(Long id) {

		Product product = getProduct(id);
		this.productRepository.delete(product);
	}

	@Override
	public Page<ProductGetOneResponseDTO> getCollection(Pageable pageable) {
		return productRepository.findAll(pageable).map(ProductMapper::toDto);

	}

	@Override
	public ProductGetOneResponseDTO getById(final Long id) {

		Product product = getProduct(id);
		return ProductMapper.toDto(product);
	}

	@Override
	@Transactional
	public void updateQuantity(Long id,
			UpdateQuantityRequestDTO updateQuantityRequestDTO) {

		getProduct(id);

		// will calculate if it will ADD or REMOVE the quantity based on the
		// Operation.
		int delta = updateQuantityRequestDTO
				.getOperation() == UpdateQuantityRequestDTO.Operation.INCREASE
						? updateQuantityRequestDTO.getQuantity()
						: -updateQuantityRequestDTO.getQuantity();

		this.productRepository.updateQuantityById(id, delta);

	}

	private Product getProduct(Long id) {

		return this.productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"Product not found with id: " + id));
	}

}
