package io.github.dariopipa.warehouse.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendEmailDTO {

	@Email
	@NotBlank
	private String to;

	@Email
	@NotBlank
	private String from;

	@NotBlank
	@Size
	private String subject;

	@NotBlank
	private String body;

	public SendEmailDTO() {
	}

	public SendEmailDTO(String to, String from, String subject, String body) {
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.body = body;
	}

	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
