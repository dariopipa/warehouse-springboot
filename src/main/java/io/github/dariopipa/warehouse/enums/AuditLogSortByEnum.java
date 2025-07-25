package io.github.dariopipa.warehouse.enums;

public enum AuditLogSortByEnum {
	created_at("createdAt"), action("action");

	private final String property;

	AuditLogSortByEnum(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}
}
