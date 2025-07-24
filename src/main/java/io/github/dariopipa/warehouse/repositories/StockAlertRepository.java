package io.github.dariopipa.warehouse.repositories;

import org.springframework.data.repository.Repository;

import io.github.dariopipa.warehouse.entities.StockAlert;

public interface StockAlertRepository extends Repository<StockAlert, Long> {

	void save(StockAlert stockAlert);
}
