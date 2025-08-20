package io.github.dariopipa.warehouse.dtos.requests;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class SendEmailDTO {

	@NotEmpty(message = "'to' must contain at least one address")
	private List<@NotBlank @Email String> to;

	@Email
	@NotBlank
	private String from;

	@NotBlank
	@Size(min = 1, max = 200)
	private String subject;

	@NotBlank
	private String body;

	public SendEmailDTO() {
	}

	public SendEmailDTO(List<String> to, String from, String subject, String body) {
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.body = body;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
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
