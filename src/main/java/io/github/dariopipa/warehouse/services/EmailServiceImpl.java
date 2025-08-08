package io.github.dariopipa.warehouse.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import io.github.dariopipa.warehouse.dtos.requests.SendEmailDTO;
import io.github.dariopipa.warehouse.services.interfaces.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

	private final Logger logger = LoggerFactory
			.getLogger(EmailServiceImpl.class);

	private final JavaMailSender javaMailSender;
	public EmailServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Override
	public void sendEmail(SendEmailDTO emailDTO) {

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(
					new InternetAddress(emailDTO.getFrom(), "Warehouse App"));
			helper.setBcc(emailDTO.getTo().toArray(new String[0]));
			helper.setSubject(emailDTO.getSubject());
			helper.setText(emailDTO.getBody(), true);

			javaMailSender.send(message);

		} catch (Exception e) {
			logger.warn("Email failed to send to {}: {}", emailDTO.getTo(),
					e.getMessage());
		}
	}
}
