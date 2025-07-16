package io.github.dariopipa.warehouse.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductTypesDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    // Getters and setters
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
