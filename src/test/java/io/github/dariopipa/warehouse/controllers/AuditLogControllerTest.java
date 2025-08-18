package io.github.dariopipa.warehouse.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;
import io.github.dariopipa.warehouse.entities.AuditLog;
import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.AuditLogSortByEnum;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.enums.SortDirectionEnum;
import io.github.dariopipa.warehouse.services.interfaces.AuditLogService;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    @Test
    void findAll_ShouldReturnPaginatedResponse_WithDefaultParameters() {
        List<AuditLog> auditLogs = Arrays.asList(
                createAuditLog(1L, "CREATE", "PRODUCT"),
                createAuditLog(2L, "UPDATE", "PRODUCT"));

        Page<AuditLog> page = new PageImpl<>(auditLogs,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")), 2);

        when(auditLogService.findAll(any(Pageable.class))).thenReturn(page);

        PaginatedResponse<AuditLog> response = auditLogController.findAll(0, 10,
                AuditLogSortByEnum.created_at, SortDirectionEnum.desc);

        assertNotNull(response);
        verify(auditLogService).findAll(any(Pageable.class));
    }

    @Test
    void findAll_ShouldReturnPaginatedResponse_WithCustomParameters() {
        List<AuditLog> auditLogs = Arrays.asList(
                createAuditLog(1L, "DELETE", "USER"));

        Page<AuditLog> page = new PageImpl<>(auditLogs,
                PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdAt")), 1);

        when(auditLogService.findAll(any(Pageable.class))).thenReturn(page);

        PaginatedResponse<AuditLog> response = auditLogController.findAll(1, 5,
                AuditLogSortByEnum.created_at, SortDirectionEnum.asc);

        assertNotNull(response);
        verify(auditLogService).findAll(any(Pageable.class));
    }

    private AuditLog createAuditLog(Long id, String action, String entityType) {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(id);
        auditLog.setAction(AuditAction.valueOf(action));
        auditLog.setEntityType(EntityType.valueOf(entityType));
        auditLog.setEntityId(1L);
        auditLog.setUserId(1L);
        auditLog.setCreatedAt(Instant.now());
        auditLog.setDetails("Test audit log details");
        return auditLog;
    }
}
