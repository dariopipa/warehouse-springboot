package io.github.dariopipa.warehouse.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class UpdateRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @PositiveOrZero
    private Integer quantity;

    @NotNull
    @PositiveOrZero
    private Integer lowStockThreshold;

    private Double weight;
    private Double height;
    private Double length;

    @NotNull
    private Long productTypeId;

    public UpdateRequestDTO() {
    }

    // Getters and setters

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

    public Long getProductTypeId() {
	return productTypeId;
    }

    public void setProductTypeId(Long productTypeId) {
	this.productTypeId = productTypeId;
    }
}
