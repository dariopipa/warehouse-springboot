package io.github.dariopipa.warehouse.enums;

public enum ProductTypeSortByEnum {
	created_at("createdAt"), name("name");

	private final String property;

	ProductTypeSortByEnum(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}
}
