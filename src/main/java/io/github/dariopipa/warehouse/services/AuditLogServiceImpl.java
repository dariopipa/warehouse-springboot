package io.github.dariopipa.warehouse.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.entities.AuditLog;
import io.github.dariopipa.warehouse.events.AuditLogEvent;
import io.github.dariopipa.warehouse.repositories.AuditLogRepository;
import io.github.dariopipa.warehouse.services.interfaces.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

	private final AuditLogRepository auditLogRepository;
	public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	};

	@Override
	public void save(AuditLogEvent event) {
		AuditLog auditLogEntity = new AuditLog(event.getUserId(),
				event.getAction(), event.getEntityType(), event.getEntityId(),
				event.getDetails());

		this.auditLogRepository.save(auditLogEntity);
	}

	@Override
	public Page<AuditLog> findAll(Pageable pageable) {
		return this.auditLogRepository.findAll(pageable);
	}

}
