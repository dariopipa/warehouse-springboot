package io.github.dariopipa.warehouse.audit;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.enums.OperationsType;
import io.github.dariopipa.warehouse.events.AuditLogEvent;

@ExtendWith(MockitoExtension.class)
class AuditLoggerTest {

	@Mock
	private ApplicationEventPublisher publisher;

	@InjectMocks
	private AuditLogger auditLogger;

	private Long userId;
	private Long entityId;

	@BeforeEach
	void setUp() {
		userId = 1L;
		entityId = 100L;
	}

	@Test
	void testLog_ShouldPublishAudit() {
		auditLogger.log(userId, AuditAction.CREATE, EntityType.PRODUCT, entityId);

		verify(publisher, times(1)).publishEvent(argThat((AuditLogEvent event) -> event.getUserId().equals(userId)
				&& event.getAction().equals(AuditAction.CREATE) && event.getEntityType().equals(EntityType.PRODUCT)
				&& event.getEntityId().equals(entityId)
				&& event.getDetails().contains("User 1 create a product with ID 100")));
	}

	@Test
	void testLog_WithUpdateAction_ShouldReturnTheCorrectMessage() {
		auditLogger.log(userId, AuditAction.UPDATE, EntityType.PRODUCT_TYPE, entityId);

		verify(publisher, times(1)).publishEvent(
				argThat((AuditLogEvent event) -> event.getDetails().contains("User 1 update a product_type with ID 100")
						&& event.getDetails().matches(".*\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
	}

	@Test
	void testLog_WithDeleteAction_ShouldReturnTheCorrectMessage() {
		auditLogger.log(userId, AuditAction.DELETE, EntityType.PRODUCT, entityId);

		verify(publisher, times(1)).publishEvent(
				argThat((AuditLogEvent event) -> event.getDetails().contains("User 1 delete a product with ID 100")));
	}

	@Test
	void testLogQuantityUpdate_WithIncreaseOperation_ShouldReturnTheCorrectMessage() {
		auditLogger.logQuantityUpdate(userId, EntityType.PRODUCT, entityId, OperationsType.INCREASE, 50);

		verify(publisher, times(1)).publishEvent(argThat((AuditLogEvent event) -> event.getUserId().equals(userId)
				&& event.getAction().equals(AuditAction.UPDATE) && event.getEntityType().equals(EntityType.PRODUCT)
				&& event.getEntityId().equals(entityId)
				&& event.getDetails().contains("User 1 updated a product with ID 100: INCREASE quantity by 50")));
	}

	@Test
	void testLogQuantityUpdate_WithDecreaseOperation_ShouldReturnTheCorrectMessage() {
		auditLogger.logQuantityUpdate(userId, EntityType.PRODUCT, entityId, OperationsType.DECREASE, 25);

		verify(publisher, times(1)).publishEvent(argThat((AuditLogEvent event) -> event.getDetails()
				.contains("User 1 updated a product with ID 100: DECREASE quantity by 25")
				&& event.getDetails().matches(".*\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
	}

	@Test
	void testLogQuantityUpdate_ShouldAlwaysUseUpdateAction() {
		auditLogger.logQuantityUpdate(userId, EntityType.PRODUCT, entityId, OperationsType.INCREASE, 10);

		verify(publisher, times(1))
				.publishEvent(argThat((AuditLogEvent event) -> event.getAction().equals(AuditAction.UPDATE)));
	}

	@Test
	void testLog_WithDifferentUserAndEntityIds_ShouldUseCorrectValues() {
		auditLogger.log(999L, AuditAction.CREATE, EntityType.PRODUCT_TYPE, 555L);

		verify(publisher, times(1)).publishEvent(
				argThat((AuditLogEvent event) -> event.getUserId().equals(999L) && event.getEntityId().equals(555L)
						&& event.getDetails().contains("User 999") && event.getDetails().contains("ID 555")));
	}

	@Test
	void testLog_ShouldIncludeTimestampInCorrectFormat() {
		auditLogger.log(userId, AuditAction.CREATE, EntityType.PRODUCT, entityId);

		verify(publisher, times(1)).publishEvent(argThat(
				(AuditLogEvent event) -> event.getDetails().matches(".*\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
	}

	@Test
	void testLog_WithUserEntityType_ShouldFormatCorrectly() {
		auditLogger.log(userId, AuditAction.CREATE, EntityType.USER, entityId);

		verify(publisher, times(1)).publishEvent(
				argThat((AuditLogEvent event) -> event.getDetails().contains("User 1 create a user with ID 100")));
	}
}
