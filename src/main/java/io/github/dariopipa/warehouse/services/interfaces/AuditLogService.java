package io.github.dariopipa.warehouse.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.dariopipa.warehouse.entities.AuditLog;
import io.github.dariopipa.warehouse.events.AuditLogEvent;

public interface AuditLogService {

	void save(AuditLogEvent event);

	Page<AuditLog> findAll(Pageable pageable);

}
