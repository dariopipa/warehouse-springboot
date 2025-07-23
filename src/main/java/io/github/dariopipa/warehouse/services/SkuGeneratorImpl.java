package io.github.dariopipa.warehouse.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.services.interfaces.SkuGeneratorService;

@Service
public class SkuGeneratorImpl implements SkuGeneratorService {

	@Override
	public String generateSku(String productName, String productTypeName) {

		String prefixFromProductName = productName.substring(0, 2)
				.toUpperCase();
		String prefixFromProductTypeName = productTypeName.substring(0, 2)
				.toUpperCase();

		int currentYear = LocalDate.now().getYear();
		long timestamp = System.currentTimeMillis() % 100_000;

		return String.format("%s-%s-%d-%05d", prefixFromProductName,
				prefixFromProductTypeName, currentYear, timestamp);
	}

}
