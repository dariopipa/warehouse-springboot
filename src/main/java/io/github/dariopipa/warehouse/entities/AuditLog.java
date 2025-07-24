package io.github.dariopipa.warehouse.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import io.github.dariopipa.warehouse.audit.AuditAction;
import io.github.dariopipa.warehouse.audit.EntityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Immutable
public class AuditLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, updatable = false)
	private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AuditAction action;

	@Enumerated(EnumType.STRING)
	@Column(name = "entity_type", nullable = false, updatable = false)
	private EntityType entityType;

	@Column(name = "entity_id", nullable = false, updatable = false)
	private Long entityId;

	@Column(nullable = false, updatable = false)
	private String details;

	@CreationTimestamp
	@Column(name = "logged_at", nullable = false, updatable = false)
	private Instant createdAt;

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public AuditAction getAction() {
		return action;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public Long getEntityId() {
		return entityId;
	}

	public String getDetails() {
		return details;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setAction(AuditAction action) {
		this.action = action;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	// JPA needed
	public AuditLog() {

	}
	public AuditLog(Long userId, AuditAction action, EntityType entityType,
			Long entityId, String details) {
		this.userId = userId;
		this.action = action;
		this.entityType = entityType;
		this.entityId = entityId;
		this.details = details;
	}

}
