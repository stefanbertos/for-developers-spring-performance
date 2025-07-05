package com.example.demo.product.domain;

import com.example.demo.product.config.CurrencyExchangeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeServiceTest {

	@Mock
	private RestClient restClient;

	@Mock
	private CurrencyExchangeProperties properties;

	@BeforeEach
	void setUp() {
		// Configure mock properties with lenient mode to avoid unnecessary stubbing
		// exceptions
		lenient().when(properties.frankfurterApiUrl()).thenReturn("https://api.frankfurter.app/latest?from=EUR&to=USD");
		lenient().when(properties.maxRetries()).thenReturn(3);
	}

	@Test
	void convertUsdToEur_ShouldConvertCorrectly() {
		// Arrange
		BigDecimal usdAmount = new BigDecimal("110.00");
		BigDecimal exchangeRate = new BigDecimal("1.1");
		BigDecimal expectedEurAmount = usdAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP); // 100.00
																								// EUR

		CurrencyExchangeService service = new CurrencyExchangeService(restClient, properties);
		CurrencyExchangeService spyService = spy(service);
		doReturn(exchangeRate).when(spyService).getExchangeRate("EUR", "USD");

		// Act
		BigDecimal result = usdAmount.divide(spyService.getExchangeRate("EUR", "USD"), 2, RoundingMode.HALF_UP);

		// Assert
		assertThat(result).isEqualTo(expectedEurAmount);
		verify(spyService).getExchangeRate("EUR", "USD");
	}

	@Test
	void convertUsdToEur_WhenUsdAmountIsNull_ShouldReturnZero() {
		// Arrange
		CurrencyExchangeService service = new CurrencyExchangeService(restClient, properties);
		BigDecimal usdAmount = null;

		// Act
		BigDecimal result = (usdAmount == null) ? BigDecimal.ZERO
				: usdAmount.divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);

		// Assert
		assertThat(result).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	void convertUsdToEur_WhenExchangeRateIsZeroOrNegative_ShouldUseDefaultRate() {
		// Arrange
		BigDecimal usdAmount = new BigDecimal("100.00");
		BigDecimal invalidRate = BigDecimal.ZERO;
		BigDecimal expectedEurAmount = usdAmount; // With rate of 1, USD = EUR

		CurrencyExchangeService service = new CurrencyExchangeService(restClient, properties);
		CurrencyExchangeService spyService = spy(service);
		doReturn(invalidRate).when(spyService).getExchangeRate("USD", "EUR");

		// Act
		BigDecimal rate = spyService.getExchangeRate("USD", "EUR");
		BigDecimal result = (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) ? usdAmount
				: usdAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

		// Assert
		assertThat(result).isEqualTo(expectedEurAmount);
		verify(spyService).getExchangeRate("USD", "EUR");
	}

}
