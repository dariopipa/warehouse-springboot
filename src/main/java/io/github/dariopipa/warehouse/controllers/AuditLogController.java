package io.github.dariopipa.warehouse.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.dariopipa.warehouse.entities.AuditLog;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/api/audit-logs")
@Tag(name = "Audit Logs")
public class AuditLogController {

	@GetMapping()
	public List<AuditLog> findAll() {

		return null;
	}
}
