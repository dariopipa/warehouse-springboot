package io.github.dariopipa.warehouse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.github.dariopipa.warehouse.entities.AuditLog;
import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.events.AuditLogEvent;
import io.github.dariopipa.warehouse.repositories.AuditLogRepository;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

	@Mock
	private AuditLogRepository auditLogRepository;

	@InjectMocks
	private AuditLogServiceImpl auditLogService;

	private AuditLogEvent auditLogEvent;
	private AuditLog auditLog;

	@BeforeEach
	void setUp() {
		auditLogEvent = new AuditLogEvent(1L, AuditAction.CREATE, EntityType.PRODUCT, 100L, "Product created");
		auditLog = new AuditLog(1L, AuditAction.CREATE, EntityType.PRODUCT, 100L, "Product created");
	}

	@Test
	void test_Save_ShouldCreateAndSaveAuditLog() {
		auditLogService.save(auditLogEvent);

		verify(auditLogRepository).save(any(AuditLog.class));
	}

	@Test
	void test_FindAll_ShouldReturnPageOfAuditLogs() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<AuditLog> expectedPage = new PageImpl<>(List.of(auditLog));

		when(auditLogRepository.findAll(pageable)).thenReturn(expectedPage);

		Page<AuditLog> result = auditLogService.findAll(pageable);

		assertEquals(expectedPage, result);
		verify(auditLogRepository).findAll(pageable);
	}

	@Test
	void test_Save_WithDifferentAction_ShouldSave() {
		AuditLogEvent updateEvent = new AuditLogEvent(2L, AuditAction.UPDATE, EntityType.PRODUCT_TYPE, 200L,
				"Product type updated");

		auditLogService.save(updateEvent);

		verify(auditLogRepository).save(any(AuditLog.class));
	}

	@Test
	void test_Save_WithDeleteAction_ShouldSave() {
		AuditLogEvent deleteEvent = new AuditLogEvent(3L, AuditAction.DELETE, EntityType.USER, 300L, "User deleted");

		auditLogService.save(deleteEvent);

		verify(auditLogRepository).save(any(AuditLog.class));
	}

	@Test
	void test_FindAll_WithDifferentPaginationParameters_ShouldCallRepository() {
		Pageable pageable = PageRequest.of(1, 5);
		Page<AuditLog> expectedPage = new PageImpl<>(List.of());

		when(auditLogRepository.findAll(pageable)).thenReturn(expectedPage);

		Page<AuditLog> result = auditLogService.findAll(pageable);

		assertEquals(expectedPage, result);
		verify(auditLogRepository).findAll(pageable);
	}
}
