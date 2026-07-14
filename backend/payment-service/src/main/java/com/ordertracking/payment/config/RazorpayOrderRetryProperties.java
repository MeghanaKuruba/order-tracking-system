package com.ordertracking.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.razorpay-order")
@Data
public class RazorpayOrderRetryProperties {

    private int maxAttempts;

    private int RetryIntervalSeconds;

}