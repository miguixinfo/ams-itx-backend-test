package com.miguixinfo.ams.backend.test.similar_products.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "product.api")
public record ProductApiProperties(String baseUrl, int timeoutSeconds) {}
