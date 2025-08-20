package io.github.dariopipa.warehouse.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import io.github.dariopipa.warehouse.dtos.requests.SendEmailDTO;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

	@Mock
	private JavaMailSender javaMailSender;

	@Mock
	private MimeMessage mimeMessage;

	@InjectMocks
	private EmailServiceImpl emailService;

	private SendEmailDTO emailDTO;

	@BeforeEach
	void setUp() {
		emailDTO = new SendEmailDTO();
		emailDTO.setFrom("sender@company.com");
		emailDTO.setTo(Arrays.asList("recipient1@company.com", "recipient2@company.com"));
		emailDTO.setSubject("Test Subject");
		emailDTO.setBody("Test email body content");
	}

	@Test
	void test_SendEmail_ShouldCreateAndSendMimeMessage() throws Exception {
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailService.sendEmail(emailDTO);

		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(1)).send(mimeMessage);
	}

	@Test
	void test_SendEmail_WithSingleRecipient_ShouldSendSuccessfully() throws Exception {
		emailDTO.setTo(Arrays.asList("single@company.com"));
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailService.sendEmail(emailDTO);

		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(1)).send(mimeMessage);
	}

	@Test
	void test_SendEmail_WhenJavaMailSenderThrowsException_ShouldHandleFine() throws Exception {
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		doThrow(new RuntimeException("Mail server error")).when(javaMailSender).send(any(MimeMessage.class));

		emailService.sendEmail(emailDTO);

		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(1)).send(mimeMessage);
	}

	@Test
	void test_SendEmail_WhenCreateMimeMessageThrowsException_ShouldHandleFine() throws Exception {
		when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

		emailService.sendEmail(emailDTO);

		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(0)).send(any(MimeMessage.class));
	}

	@Test
	void test_SendEmail_WithEmptyRecipientList_ShouldAttemptToSend() throws Exception {
		emailDTO.setTo(Arrays.asList());
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailService.sendEmail(emailDTO);

		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(1)).send(mimeMessage);
	}

	@Test
	void test_SendEmail_WithNullBody_ShouldHandleFine() throws Exception {
		emailDTO.setBody(null);
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailService.sendEmail(emailDTO);

		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(0)).send(any(MimeMessage.class));
	}

	@Test
	void test_SendEmail_WithLongSubjectAndBody_ShouldSendSuccessfully() throws Exception {
		emailDTO.setSubject("Very Long Subject ".repeat(10));
		emailDTO.setBody("Very Long Body Content ".repeat(100));
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailService.sendEmail(emailDTO);

		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(1)).send(mimeMessage);
	}
}
