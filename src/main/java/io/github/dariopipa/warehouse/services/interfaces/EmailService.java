package io.github.dariopipa.warehouse.services.interfaces;

import io.github.dariopipa.warehouse.dtos.requests.SendEmailDTO;

public interface EmailService {
	void sendEmail(SendEmailDTO emailDTO);
}
