package com.example.demo.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for currency exchange service.
 */
@Configuration
@EnableConfigurationProperties(CurrencyExchangeProperties.class)
public class CurrencyExchangeConfig {
}