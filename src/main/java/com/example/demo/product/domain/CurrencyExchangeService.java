package com.example.demo.product.domain;

import com.example.demo.product.config.CurrencyExchangeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service for currency exchange operations.
 */
@Service
public class CurrencyExchangeService {

	private static final Logger log = LoggerFactory.getLogger(CurrencyExchangeService.class);

	private final RestClient restClient;

	private final CurrencyExchangeProperties properties;

	public CurrencyExchangeService(RestClient restClient, CurrencyExchangeProperties properties) {
		this.restClient = restClient;
		this.properties = properties;
	}

	/**
	 * Record representing a response from the Frankfurter API.
	 */
	public record FrankfurterResponse(String amount, String base, String date, Map<String, BigDecimal> rates) {
	}

	/**
	 * Get the exchange rate from one currency to another.
	 * @param from the source currency
	 * @param to the target currency
	 * @return the exchange rate
	 */
	@Cacheable(value = "currency", key = "#from + '-' + #to")
	@Retryable(maxAttempts = 3, retryFor = { Exception.class }, noRetryFor = { IllegalArgumentException.class })
	public BigDecimal getExchangeRate(String from, String to) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching {} to {} exchange rate from Frankfurter API", from, to);
		}

		FrankfurterResponse response = restClient.get()
			.uri(properties.frankfurterApiUrl() + "from=" + from + "&to=" + to)
			.retrieve()
			.body(FrankfurterResponse.class);

		if (response != null && response.rates() != null && response.rates().containsKey(to)) {
			BigDecimal rate = response.rates().get(to);
			if (log.isDebugEnabled()) {
				log.debug("Received {} to {} exchange rate: {}", from, to, rate);
			}
			return rate;
		}
		else {
			log.warn("Invalid response from Frankfurter API");
			throw new RuntimeException("Invalid response from Frankfurter API");
		}
	}

	/**
	 * Recovery method for getEurToUsdRate.
	 * @param e the exception that caused the retry to fail
	 * @return default exchange rate (1.0)
	 */
	@Recover
	public BigDecimal getExchangeRate(Exception e) {
		log.error("Error fetching exchange rate from Frankfurter API after retries", e);
		return BigDecimal.ONE; // Default to 1:1 if API fails after all retries
	}

}
