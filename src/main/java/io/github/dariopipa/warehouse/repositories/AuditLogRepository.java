package io.github.dariopipa.warehouse.repositories;

import java.util.List;

import org.springframework.data.repository.Repository;

import io.github.dariopipa.warehouse.entities.AuditLog;

public interface AuditLogRepository extends Repository<AuditLog, Long> {

	void save(AuditLog auditLog);

	List<AuditLog> findAll();

}
