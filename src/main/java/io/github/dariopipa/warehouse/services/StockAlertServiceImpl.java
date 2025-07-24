package io.github.dariopipa.warehouse.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.StockAlert;
import io.github.dariopipa.warehouse.repositories.StockAlertRepository;
import io.github.dariopipa.warehouse.services.interfaces.EmailService;
import io.github.dariopipa.warehouse.services.interfaces.StockAlertService;

@Service
public class StockAlertServiceImpl implements StockAlertService {

	private final StockAlertRepository stockAlertRepository;
	private final EmailService emailService;

	private final Logger logger = LoggerFactory
			.getLogger(StockAlertServiceImpl.class);

	public StockAlertServiceImpl(StockAlertRepository stockAlertRepository,
			EmailService emailService) {
		this.stockAlertRepository = stockAlertRepository;
		this.emailService = emailService;
	}

	@Override
	public void alertStockLow(Product product, int newQuantity) {

		if (newQuantity >= product.getLowStockThreshold()) {
			return;
		}

		this.emailService.sendEmail();

		StockAlert alert = new StockAlert();
		alert.setProduct(product);
		alert.setEmailSent(true);

		this.stockAlertRepository.save(alert);

		return;
	}

}
