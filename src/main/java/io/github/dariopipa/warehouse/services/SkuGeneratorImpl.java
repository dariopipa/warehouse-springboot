package io.github.dariopipa.warehouse.services;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.controllers.ProductsController;
import io.github.dariopipa.warehouse.services.interfaces.SkuGeneratorService;

@Service
public class SkuGeneratorImpl implements SkuGeneratorService {

	private final Logger logger = LoggerFactory
			.getLogger(SkuGeneratorImpl.class);

	@Override
	public String generateSku(String productName, String productTypeName) {
		logger.info("Generating SKU for product: {} and type: {}", productName,
				productTypeName);

		String prefixFromProductName = productName.substring(0, 2)
				.toUpperCase();
		String prefixFromProductTypeName = productTypeName.substring(0, 2)
				.toUpperCase();

		int currentYear = LocalDate.now().getYear();
		long timestamp = System.currentTimeMillis() % 100_000;
		String sku = String.format("%s-%s-%d-%05d", prefixFromProductName,
				prefixFromProductTypeName, currentYear, timestamp);

		logger.debug("Generated SKU: {}", sku);
		return sku;
	}

}
