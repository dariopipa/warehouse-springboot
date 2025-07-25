package io.github.dariopipa.warehouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.dariopipa.warehouse.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
