package io.github.dariopipa.warehouse.dtos.responses;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductTypeResponseDTO {

	@NotBlank(message = "Id is required")
	private Long id;
	
	@NotBlank(message = "Name is required")
	@Size(max = 255, message = "Name must be at most 255 characters")
	private String name;

    @JsonProperty("created_at")
	private Instant createdAt;

    @JsonProperty("updated_at")
	private Instant updatedAt;
	
    @JsonProperty("created_by")
	private Long createdBy;
	
    @JsonProperty("updated_by")
	private Long updatedBy;
	
	// Getters and setters
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

}
