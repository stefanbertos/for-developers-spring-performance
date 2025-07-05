package com.example.demo.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private EmailService emailService;

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
