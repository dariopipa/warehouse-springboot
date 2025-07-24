package io.github.dariopipa.warehouse.services.interfaces;

import io.github.dariopipa.warehouse.entities.Product;

public interface StockAlertService {
	void alertStockLow(Product product, int newQuantity);
}
