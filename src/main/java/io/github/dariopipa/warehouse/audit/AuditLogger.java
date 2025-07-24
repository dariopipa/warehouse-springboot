package io.github.dariopipa.warehouse.audit;

import java.time.Instant;

import org.springframework.context.ApplicationEventPublisher;

import io.github.dariopipa.warehouse.events.AuditLogEvent;

public class AuditLogger {

	private final ApplicationEventPublisher publisher;

	public AuditLogger(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void log(Long userId, AuditAction action, EntityType entityType,
			Long entityId) {
		
		String details = String.format("User %d %s a %s with ID %d at %s",
				userId, action.name().toLowerCase(),
				entityType.name().toLowerCase(), entityId, Instant.now());

		AuditLogEvent event = new AuditLogEvent(userId, action, entityType,
				entityId, details);
		publisher.publishEvent(event);
	}
}