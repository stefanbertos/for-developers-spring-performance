package com.example.demo.common.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

/**
 * Configuration for RestClient.
 */
@Configuration
class RestClientConfig {

	private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);

	/**
	 * Creates a RestClient bean with default configuration.
	 * @return the RestClient
	 */
	@Bean
	public RestClient restClient() {
		return RestClient.builder().requestInterceptor((request, body, execution) -> {
			if (log.isDebugEnabled()) {
				log.debug("Outgoing Request: {} {} Headers: {} Body: {}", request.getMethod(), request.getURI(),
						request.getHeaders(), new String(body, StandardCharsets.UTF_8));
			}
			return execution.execute(request, body);
		})
			.defaultStatusHandler(status -> status.is4xxClientError() || status.is5xxServerError(),
					(request, response) -> {
						throw new RuntimeException("Error calling external API: " + response);
					})
			.build();
	}

}