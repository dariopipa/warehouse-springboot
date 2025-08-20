package io.github.dariopipa.warehouse.mappers;

import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.ProductType;

public class ProductMapper {

	public static Product toEntity(CreateProductDTO dto, Long userId, ProductType productType, String generatedSku) {
		Product product = new Product();

		product.setSku(generatedSku);

		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setQuantity(dto.getQuantity());
		product.setLowStockThreshold(dto.getLowStockThreshold());

		product.setWeight(dto.getWeight());
		product.setHeight(dto.getHeight());
		product.setLength(dto.getLength());

		product.setProductType(productType);
		product.setCreatedBy(userId);
		product.setUpdatedBy(userId);

		return product;
	}

	public static ProductGetOneResponseDTO toDto(Product productEntity) {
		ProductGetOneResponseDTO dto = new ProductGetOneResponseDTO();

		dto.setId(productEntity.getId());
		dto.setName(productEntity.getName());
		dto.setSku(productEntity.getSku());
		dto.setDescription(productEntity.getDescription());
		dto.setQuantity(productEntity.getQuantity());
		dto.setLowStockThreshold(productEntity.getLowStockThreshold());
		dto.setWeight(productEntity.getWeight());
		dto.setHeight(productEntity.getHeight());
		dto.setLength(productEntity.getLength());
		dto.setCreatedAt(productEntity.getCreatedAt());
		dto.setCreatedBy(productEntity.getCreatedBy());
		dto.setUpdatedAt(productEntity.getUpdatedAt());
		dto.setUpdatedBy(productEntity.getUpdatedBy());
		dto.setProductType(new ProductGetOneResponseDTO.ProductTypeDTO(productEntity.getProductType().getId(),
				productEntity.getProductType().getName()));

		return dto;
	}

	public static Product updateEntityFromDto(UpdateProductRequestDTO dto, Product existingProduct,
			ProductType productType) {
		// Keep immutable fields
		existingProduct.setSku(existingProduct.getSku());

		// Update mutable fields
		existingProduct.setName(dto.getName());
		existingProduct.setDescription(dto.getDescription());
		existingProduct.setQuantity(dto.getQuantity());
		existingProduct.setLowStockThreshold(dto.getLowStockThreshold());
		existingProduct.setWeight(dto.getWeight());
		existingProduct.setHeight(dto.getHeight());
		existingProduct.setLength(dto.getLength());
		existingProduct.setProductType(productType);

		return existingProduct;
	}

}
