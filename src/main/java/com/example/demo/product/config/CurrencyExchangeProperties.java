package com.example.demo.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * Configuration properties for currency exchange service.
 */
@ConfigurationProperties(prefix = "currency.exchange")
public record CurrencyExchangeProperties(String frankfurterApiUrl, int maxRetries) {

	/**
	 * Creates a new instance of CurrencyExchangeProperties.
	 * @param frankfurterApiUrl the URL for the Frankfurter API
	 * @param maxRetries the maximum number of retries for API calls
	 */
	@ConstructorBinding
	public CurrencyExchangeProperties {
		if (maxRetries < 0) {
			throw new IllegalArgumentException("maxRetries must be non-negative");
		}
	}
}
