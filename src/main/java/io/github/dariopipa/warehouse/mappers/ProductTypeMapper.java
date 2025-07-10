package io.github.dariopipa.warehouse.mappers;

import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;

public class ProductTypeMapper {

	public static ProductType toEntity(ProductTypesDTO dto, Long userId) {
		ProductType entity = new ProductType();
		entity.setName(dto.getName());
		entity.setCreatedBy(userId);
		entity.setUpdatedBy(userId);
		return entity;
	}

	public static ProductTypeResponseDTO toDto(ProductType productType) {
		ProductTypeResponseDTO dto = new ProductTypeResponseDTO();
		dto.setId(productType.getId());
		dto.setName(productType.getName());
		dto.setCreatedAt(productType.getCreatedAt());
		dto.setCreatedBy(productType.getCreatedBy());
		dto.setUpdatedAt(productType.getUpdatedAt());
		dto.setUpdatedBy(productType.getUpdatedBy());
		return dto;
	}

}
