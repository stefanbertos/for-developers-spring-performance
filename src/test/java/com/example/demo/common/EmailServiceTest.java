package com.example.demo.common;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EmailServiceTest {

	@Autowired
	private EmailService emailService;

	@MockBean
	private JavaMailSender mailSender;

	@Test
	void sendSimpleMessage_sendsEmail() {
		emailService.sendSimpleMessage("to@example.com", "Test Subject", "Test Body");
		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());
		SimpleMailMessage sent = captor.getValue();
		assertThat(sent.getTo()).containsExactly("to@example.com");
		assertThat(sent.getSubject()).isEqualTo("Test Subject");
		assertThat(sent.getText()).isEqualTo("Test Body");
	}

}
