package com.example.demo.product.domain;

import com.example.demo.product.config.CurrencyExchangeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
	 * Get the exchange rate from EUR to USD.
	 * @return the exchange rate
	 */
	@Retryable(maxAttempts = 3, retryFor = { Exception.class }, noRetryFor = { IllegalArgumentException.class })
	public BigDecimal getEurToUsdRate() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching EUR to USD exchange rate from Frankfurter API");
		}

		FrankfurterResponse response = restClient.get()
			.uri(properties.frankfurterApiUrl())
			.retrieve()
			.body(FrankfurterResponse.class);

		if (response != null && response.rates() != null && response.rates().containsKey("USD")) {
			BigDecimal rate = response.rates().get("USD");
			if (log.isDebugEnabled()) {
				log.debug("Received EUR to USD exchange rate: {}", rate);
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
	public BigDecimal recoverGetEurToUsdRate(Exception e) {
		log.error("Error fetching exchange rate from Frankfurter API after retries", e);
		return BigDecimal.ONE; // Default to 1:1 if API fails after all retries
	}

	/**
	 * Convert USD to EUR.
	 * @param usdAmount amount in USD
	 * @return amount in EUR
	 */
	public BigDecimal convertUsdToEur(BigDecimal usdAmount) {
		if (usdAmount == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal rate = getEurToUsdRate();
		if (rate.compareTo(BigDecimal.ZERO) <= 0) {
			log.warn("Invalid exchange rate: {}, using 1:1", rate);
			rate = BigDecimal.ONE;
		}

		return usdAmount.divide(rate, 2, RoundingMode.HALF_UP);
	}

	/**
	 * Record representing a response from the Frankfurter API.
	 */
	public record FrankfurterResponse(String amount, String base, String date, Map<String, BigDecimal> rates) {
	}

}
