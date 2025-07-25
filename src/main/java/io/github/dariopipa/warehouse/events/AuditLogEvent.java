package io.github.dariopipa.warehouse.events;

import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;

public class AuditLogEvent {
	private final Long userId;
	private final AuditAction action;
	private final EntityType entityType;
	private final Long entityId;
	private final String details;

	public AuditLogEvent(Long userId, AuditAction action, EntityType entityType,
			Long entityId, String details) {
		this.userId = userId;
		this.action = action;
		this.entityType = entityType;
		this.entityId = entityId;
		this.details = details;
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
}
