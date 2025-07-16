package io.github.dariopipa.warehouse.dtos.responses;

import java.time.Instant;
import java.util.UUID;

public class ProductGetOneResponseDTO {

    private Long id;
    private UUID uuid;
    private String sku;
    private String name;
    private String description;
    private Integer quantity;
    private Integer lowStockThreshold;
    private Double weight;
    private Double height;
    private Double length;
    private Instant createdAt;
    private Instant updatedAt;
    private Long createdBy;
    private Long updatedBy;

    private ProductTypeDTO productType;

    public static class ProductTypeDTO {
	private Long id;
	private String name;

	public ProductTypeDTO() {
	}

	public ProductTypeDTO(Long id, String name) {
	    this.id = id;
	    this.name = name;
	}

	public Long getId() {
	    return id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
    }

    public ProductGetOneResponseDTO() {
    }

    // All Getters and Setters

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public UUID getUuid() {
	return uuid;
    }

    public void setUuid(UUID uuid) {
	this.uuid = uuid;
    }

    public String getSku() {
	return sku;
    }

    public void setSku(String sku) {
	this.sku = sku;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Integer getQuantity() {
	return quantity;
    }

    public void setQuantity(Integer quantity) {
	this.quantity = quantity;
    }

    public Integer getLowStockThreshold() {
	return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
	this.lowStockThreshold = lowStockThreshold;
    }

    public Double getWeight() {
	return weight;
    }

    public void setWeight(Double weight) {
	this.weight = weight;
    }

    public Double getHeight() {
	return height;
    }

    public void setHeight(Double height) {
	this.height = height;
    }

    public Double getLength() {
	return length;
    }

    public void setLength(Double length) {
	this.length = length;
    }

    public Instant getCreatedAt() {
	return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
	this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
	return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
	this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
	return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
	this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
	return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
	this.updatedBy = updatedBy;
    }

    public ProductTypeDTO getProductType() {
	return productType;
    }

    public void setProductType(ProductTypeDTO productType) {
	this.productType = productType;
    }
}
