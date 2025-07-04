package com.example.demo.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Value;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestPropertySource(properties = { "embedded.mailhog.enabled=true" })
class EmailServiceIntegrationTest {

	@Autowired
	private EmailService emailService;

	@Value("${embedded.mailhog.smtp.host}")
	private String smtpHost;

	@Value("${embedded.mailhog.smtp.port}")
	private int smtpPort;

	@Value("${embedded.mailhog.http.host}")
	private String httpHost;

	@Value("${embedded.mailhog.http.port}")
	private int httpPort;

	@DynamicPropertySource
	static void mailhogProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.mail.host", () -> System.getProperty("embedded.mailhog.smtp.host", "localhost"));
		registry.add("spring.mail.port",
				() -> Integer.parseInt(System.getProperty("embedded.mailhog.smtp.port", "1025")));
		registry.add("spring.mail.username", () -> "");
		registry.add("spring.mail.password", () -> "");
		registry.add("spring.mail.properties.mail.smtp.auth", () -> "false");
		registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "false");
	}

	@Test
	void sendSimpleMessage_sendsEmailToMailhog() throws Exception {
		String to = "receiver@fake.com";
		String subject = "Integration Subject";
		String text = "Integration Body";

		emailService.sendSimpleMessage(to, subject, text);

		// Wait for MailHog to receive the email
		Thread.sleep(1000);

		// Fetch emails from MailHog API
		HttpClient client = HttpClient.newHttpClient();
		String apiUrl = String.format("http://%s:%d/api/v2/messages", httpHost, httpPort);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response.body());
		JsonNode items = root.get("items");
		assertThat(items).isNotNull();
		assertThat(items.size()).isGreaterThan(0);
		JsonNode message = items.get(0);
		String subjectReceived = message.at("/Content/Headers/Subject/0").asText();
		String body = message.at("/Content/Body").asText();
		assertThat(subjectReceived).isEqualTo(subject);
		assertThat(body).contains(text);
	}

}
