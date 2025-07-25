package io.github.dariopipa.warehouse.eventListeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.github.dariopipa.warehouse.events.AuditLogEvent;
import io.github.dariopipa.warehouse.services.interfaces.AuditLogService;

@Component
public class AuditEventListener {

	private final AuditLogService auditLogService;

	public AuditEventListener(AuditLogService auditLogService) {
		this.auditLogService = auditLogService;
	}

	@EventListener
	public void handleAuditEvent(AuditLogEvent event) {
		auditLogService.save(event);
	}
}
