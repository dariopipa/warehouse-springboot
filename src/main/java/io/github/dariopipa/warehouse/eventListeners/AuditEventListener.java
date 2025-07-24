package io.github.dariopipa.warehouse.eventListeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.github.dariopipa.warehouse.entities.AuditLog;
import io.github.dariopipa.warehouse.events.AuditLogEvent;
import io.github.dariopipa.warehouse.repositories.AuditLogRepository;

@Component
public class AuditEventListener {

	private final AuditLogRepository auditLogRepository;

	public AuditEventListener(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	@EventListener
	public void handleAuditEvent(AuditLogEvent event) {
		AuditLog log = new AuditLog(event.getUserId(), event.getAction(),
				event.getEntityType(), event.getEntityId(), event.getDetails());

		auditLogRepository.save(log);
	}
}
