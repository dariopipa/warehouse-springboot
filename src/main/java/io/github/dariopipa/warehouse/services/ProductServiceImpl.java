package io.github.dariopipa.warehouse.services;

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.mappers.ProductMapper;
import io.github.dariopipa.warehouse.repositories.ProductRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductService;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductServiceImpl implements ProductService {

    private final Long id = 1L;

    private final ProductRepository productRepository;
    private final ProductTypeService productTypeService;

    public ProductServiceImpl(ProductRepository productRepository, ProductTypeService productTypeService) {
	this.productRepository = productRepository;
	this.productTypeService = productTypeService;
    }

    @Override
    public Long save(CreateProductDTO dto) {

	Boolean existsByName = this.productRepository.existsByName(dto.getName());
	if (existsByName) {
	    throw new ResponseStatusException(HttpStatus.CONFLICT, "Product name already exists");
	}

	Boolean existsBySku = this.productRepository.existsBySku("sku-generator");
	if (existsBySku) {
	    throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
	}

	ProductType productType = productTypeService.getProductType(dto.getProductTypeId());
	Product productEntity = ProductMapper.toEntity(dto, id, productType);
	Product savedEntity = this.productRepository.save(productEntity);

	return savedEntity.getId();
    }

    @Override
    public void update(Long id, UpdateRequestDTO updateRequestDTO) {

	Product product = getProduct(id);
	ProductType productType = productTypeService.getProductType(updateRequestDTO.getProductTypeId());

	this.productRepository.save(ProductMapper.updateEntityFromDto(updateRequestDTO, product, productType));
    }

    @Override
    public void delete(Long id) {

	Product product = getProduct(id);
	this.productRepository.delete(product);
    }

    @Override
    public List<Product> getCollection() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ProductGetOneResponseDTO getById(final Long id) {

	Product product = getProduct(id);
	return ProductMapper.toDto(product);
    }

    @Override
    @Transactional
    public void updateQuantity(Long id, UpdateQuantityRequestDTO updateQuantityRequestDTO) {

	getProduct(id);

	int delta = updateQuantityRequestDTO.getOperation() == UpdateQuantityRequestDTO.Operation.INCREASE
		? updateQuantityRequestDTO.getQuantity()
		: -updateQuantityRequestDTO.getQuantity();

	productRepository.updateQuantityById(id, delta);

    }

    private Product getProduct(Long id) {

	return productRepository.findById(id)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }
}
