package io.github.dariopipa.warehouse.services;

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.mappers.ProductMapper;
import io.github.dariopipa.warehouse.repositories.ProductRepository;
import io.github.dariopipa.warehouse.repositories.ProductTypeRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductServiceImpl implements ProductService {

    private final Long id = 1L;

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductTypeRepository productTypeRepository) {
	this.productRepository = productRepository;
	this.productTypeRepository = productTypeRepository;
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

	ProductType productType = this.productTypeRepository.findById(id)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product type not found"));

	Product productEntity = ProductMapper.toEntity(dto, id, productType);
	Product savedEntity = this.productRepository.save(productEntity);

	return savedEntity.getId();
    }

    @Override
    public void update(Long id, UpdateRequestDTO updateRequestDTO) {
	Product product = this.productRepository.findById(id)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

	ProductType productType = this.productTypeRepository.findById(updateRequestDTO.getProductTypeId())
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product type not found"));

	this.productRepository.save(ProductMapper.updateEntityFromDto(updateRequestDTO, product, productType));
    }

    @Override
    public void delete(Long id) {
	Product product = this.productRepository.findById(id)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

	this.productRepository.delete(product);
    }

    @Override
    public List<Product> getCollection() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ProductGetOneResponseDTO getById(final Long id) {
	Product product = this.productRepository.findById(id)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product type not found"));

	return ProductMapper.toDto(product);
    }

    @Override
    public void updateQuantity(Long id, Product product) {
	// TODO Auto-generated method stub

    }

}
