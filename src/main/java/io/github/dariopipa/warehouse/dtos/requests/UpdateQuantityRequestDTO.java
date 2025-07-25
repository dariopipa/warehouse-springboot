package io.github.dariopipa.warehouse.dtos.requests;

import io.github.dariopipa.warehouse.enums.OperationsType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateQuantityRequestDTO {

	@NotNull(message = "Operation must not be null")
	private OperationsType operation;

	@NotNull(message = "Quantity must not be null")
	@Min(value = 1, message = "Quantity must be greater than or equal to 1")
	private Integer quantity;

	public OperationsType getOperation() {
		return operation;
	}

	public void setOperation(OperationsType operation) {
		this.operation = operation;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
