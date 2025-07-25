package io.github.dariopipa.warehouse.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;
import io.github.dariopipa.warehouse.entities.AuditLog;
import io.github.dariopipa.warehouse.enums.AuditLogSortByEnum;
import io.github.dariopipa.warehouse.enums.SortDirectionEnum;
import io.github.dariopipa.warehouse.services.interfaces.AuditLogService;
import io.github.dariopipa.warehouse.utils.PaginationUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/v1/api/audit-logs")
@Tag(name = "Audit Logs")
public class AuditLogController {

	private final AuditLogService auditLogService;
	private final Logger logger = LoggerFactory
			.getLogger(AuditLogController.class);

	public AuditLogController(AuditLogService auditLogService) {
		this.auditLogService = auditLogService;
	}

	@GetMapping()
	public PaginatedResponse<AuditLog> findAll(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(defaultValue = "created_at") AuditLogSortByEnum sortBy,
			@RequestParam(defaultValue = "desc") SortDirectionEnum direction) {

		logger.info(
				"Fetching audit logs collection - page: {}, size: {}, sortBy: {}, direction: {}",
				page, size, sortBy, direction);

		String sortColumn = sortBy.getProperty();
		Sort.Direction sortDirection = Sort.Direction
				.fromString(direction.name());

		Sort sort = Sort.by(sortDirection, sortColumn);
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<AuditLog> paginatedResponse = auditLogService.findAll(pageable);

		return PaginationUtils.buildPaginatedResponse(paginatedResponse);
	}
}
