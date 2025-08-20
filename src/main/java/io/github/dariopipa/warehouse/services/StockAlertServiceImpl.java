package io.github.dariopipa.warehouse.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.dtos.requests.SendEmailDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.StockAlert;
import io.github.dariopipa.warehouse.enums.RolesEnum;
import io.github.dariopipa.warehouse.repositories.StockAlertRepository;
import io.github.dariopipa.warehouse.services.interfaces.AuthService;
import io.github.dariopipa.warehouse.services.interfaces.EmailService;
import io.github.dariopipa.warehouse.services.interfaces.StockAlertService;

@Service
public class StockAlertServiceImpl implements StockAlertService {

	@Value("${DEFAULT_COMPANY_EMAIL}")
	private String DEFAULT_COMPANY_EMAIL;
	private final StockAlertRepository stockAlertRepository;
	private final EmailService emailService;
	private final AuthService authService;

	private final Logger logger = LoggerFactory.getLogger(StockAlertServiceImpl.class);

	public StockAlertServiceImpl(StockAlertRepository stockAlertRepository, EmailService emailService,
			AuthService authService) {
		this.stockAlertRepository = stockAlertRepository;
		this.emailService = emailService;
		this.authService = authService;
	}

	@Override
	public void alertStockLow(Product product, int newQuantity) {
		if (!isStockBelowThreshold(product, newQuantity)) {
			logger.debug("Stock level for product '{}' is OK. Quantity: {}, Threshold: {}", product.getName(),
					newQuantity, product.getLowStockThreshold());
			return;
		}

		logger.warn("Low stock detected for product '{}'. Current quantity: {}, Threshold: {}", product.getName(),
				newQuantity, product.getLowStockThreshold());

		boolean emailSent = sendLowStockEmail(product, newQuantity);
		saveStockAlert(product, emailSent);
	}

	private boolean isStockBelowThreshold(Product product, int quantity) {
		return quantity < product.getLowStockThreshold();
	}

	private boolean sendLowStockEmail(Product product, int currentQuantity) {
		try {

			List<String> recipients = authService.findEmailsByRole(RolesEnum.ROLE_MANAGER);

			SendEmailDTO emailDTO = createLowStockEmailDTO(product, currentQuantity, recipients);
			emailService.sendEmail(emailDTO);

			logger.info("Low stock email sent successfully for product '{}' to {}", product.getName(),
					emailDTO.getTo());
			return true;

		} catch (Exception e) {

			logger.warn("Failed to send low stock email for product '{}': {}", product.getName(), e.getMessage());
			return false;
		}
	}

	private SendEmailDTO createLowStockEmailDTO(Product product, int currentQuantity, List<String> recipients) {

		SendEmailDTO emailDTO = new SendEmailDTO();
		emailDTO.setTo(recipients);
		emailDTO.setFrom(DEFAULT_COMPANY_EMAIL);
		emailDTO.setSubject("Low Stock Alert: " + product.getName());
		emailDTO.setBody(createEmailBody(product, currentQuantity));
		return emailDTO;
	}

	private void saveStockAlert(Product product, boolean emailSent) {
		StockAlert alert = new StockAlert();
		alert.setProduct(product);
		alert.setEmailSent(emailSent);

		stockAlertRepository.save(alert);
		logger.info("Stock alert saved for product '{}' with emailSent={}", product.getName(), emailSent);
	}

	private String createEmailBody(Product product, int currentQuantity) {
		return String.format("""
				Warning: The stock for product '%s' is low!

				Current quantity: %d
				Threshold: %d

				Please restock as soon as possible.
				""", product.getName(), currentQuantity, product.getLowStockThreshold());
	}
}
