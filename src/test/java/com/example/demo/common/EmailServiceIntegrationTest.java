package com.example.demo.common;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EmailServiceIntegrationTest {

	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP).withPerMethodLifecycle(false);

	@Autowired
	private EmailService emailService;

	@Autowired
	private JavaMailSenderImpl mailSender;

	@Test
	void sendSimpleMessage_sendsEmailToGreenMail() throws Exception {
		String to = "receiver@fake.com";
		String subject = "Integration Subject";
		String text = "Integration Body";

		// Configure mail sender to use GreenMail
		mailSender.setHost("localhost");
		mailSender.setPort(greenMail.getSmtp().getPort());
		mailSender.setUsername("");
		mailSender.setPassword("");
		mailSender.getJavaMailProperties().put("mail.smtp.auth", "false");
		mailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "false");

		emailService.sendSimpleMessage(to, subject, text);

		// Wait for GreenMail to receive the email
		greenMail.waitForIncomingEmail(1);
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		assertThat(receivedMessages).hasSize(1);
		MimeMessage received = receivedMessages[0];
		assertThat(received.getSubject()).isEqualTo(subject);
		assertThat(received.getAllRecipients()[0].toString()).isEqualTo(to);
		assertThat(received.getContent().toString()).contains(text);
	}

}
