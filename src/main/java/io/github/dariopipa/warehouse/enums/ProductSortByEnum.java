package io.github.dariopipa.warehouse.enums;

public enum ProductSortByEnum {
	name("name"), created_at("createdAt");

	private final String property;

	ProductSortByEnum(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

}
