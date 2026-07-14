package com.ordertracking.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.expiry")
@Data
public class PaymentExpiryProperties {

    private int maxAttempts;

    private int expiryMinutes;

}