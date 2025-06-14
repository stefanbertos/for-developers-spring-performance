package com.example.demo.product.domain;

import com.example.demo.product.config.CurrencyExchangeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
        // Configure mock properties with lenient mode to avoid unnecessary stubbing exceptions
        lenient().when(properties.frankfurterApiUrl()).thenReturn("https://api.frankfurter.app/latest?from=EUR&to=USD");
        lenient().when(properties.maxRetries()).thenReturn(3);
    }

    @Test
    void convertUsdToEur_ShouldConvertCorrectly() {
        // Arrange
        BigDecimal usdAmount = new BigDecimal("110.00");
        BigDecimal exchangeRate = new BigDecimal("1.1");
        BigDecimal expectedEurAmount = usdAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP); // 100.00 EUR

        // Create a spy of CurrencyExchangeService that returns a fixed exchange rate
        CurrencyExchangeService service = new CurrencyExchangeService(restClient, properties);
        CurrencyExchangeService spyService = spy(service);
        doReturn(exchangeRate).when(spyService).getEurToUsdRate();

        // Act
        BigDecimal result = spyService.convertUsdToEur(usdAmount);

        // Assert
        assertThat(result).isEqualTo(expectedEurAmount);
        verify(spyService).getEurToUsdRate();
    }

    @Test
    void convertUsdToEur_WhenUsdAmountIsNull_ShouldReturnZero() {
        // Arrange
        CurrencyExchangeService service = new CurrencyExchangeService(restClient, properties);

        // Act
        BigDecimal result = service.convertUsdToEur(null);

        // Assert
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void convertUsdToEur_WhenExchangeRateIsZeroOrNegative_ShouldUseDefaultRate() {
        // Arrange
        BigDecimal usdAmount = new BigDecimal("100.00");
        BigDecimal invalidRate = BigDecimal.ZERO;
        BigDecimal expectedEurAmount = usdAmount; // With rate of 1, USD = EUR

        // Create a spy of CurrencyExchangeService that returns an invalid exchange rate
        CurrencyExchangeService service = new CurrencyExchangeService(restClient, properties);
        CurrencyExchangeService spyService = spy(service);
        doReturn(invalidRate).when(spyService).getEurToUsdRate();

        // Act
        BigDecimal result = spyService.convertUsdToEur(usdAmount);

        // Assert
        assertThat(result).isEqualTo(expectedEurAmount);
        verify(spyService).getEurToUsdRate();
    }
}
