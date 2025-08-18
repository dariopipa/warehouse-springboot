package io.github.dariopipa.warehouse.audit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.enums.OperationsType;
import io.github.dariopipa.warehouse.events.AuditLogEvent;

@Component
public class AuditLogger {

    private final ApplicationEventPublisher publisher;

    public AuditLogger(ApplicationEventPublisher publisher) {
	this.publisher = publisher;
    }

    public void log(Long userId, AuditAction action, EntityType entityType, Long entityId) {

	String details = String.format("User %d %s a %s with ID %d at %s", userId, action.name().toLowerCase(),
		entityType.name().toLowerCase(), entityId, getCurrentTimestamp());

	createAndPublishEvent(userId, action, entityType, entityId, details);
    }

    public void logQuantityUpdate(Long userId, EntityType entityType, Long entityId, OperationsType operation,
	    int quantityChange) {

	String details = String.format("User %d updated a %s with ID %d: %s quantity by %d at %s", userId,
		entityType.name().toLowerCase(), entityId, operation.name(), quantityChange, getCurrentTimestamp());

	createAndPublishEvent(userId, AuditAction.UPDATE, entityType, entityId, details);
    }

    private void createAndPublishEvent(Long userId, AuditAction action, EntityType entityType, Long entityId,
	    String details) {

	AuditLogEvent event = new AuditLogEvent(userId, action, entityType, entityId, details);
	publisher.publishEvent(event);
    }

    private String getCurrentTimestamp() {
	return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}