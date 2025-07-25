package io.github.dariopipa.warehouse.services.interfaces;

import java.util.List;

import io.github.dariopipa.warehouse.entities.AuditLog;
import io.github.dariopipa.warehouse.events.AuditLogEvent;

public interface AuditLogService {

	void save(AuditLogEvent event);

	List<AuditLog> findAll();

}
