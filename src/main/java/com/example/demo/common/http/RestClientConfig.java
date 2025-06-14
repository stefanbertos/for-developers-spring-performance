package com.example.demo.common.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configuration for RestClient.
 */
@Configuration
class RestClientConfig {

    /**
     * Creates a RestClient bean with default configuration.
     * @return the RestClient
     */
    @Bean
    RestClient restClient() {
        return RestClient.builder()
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            throw new RuntimeException("Error calling external API: " + response.getStatusCode());
                        })
                .build();
    }
}