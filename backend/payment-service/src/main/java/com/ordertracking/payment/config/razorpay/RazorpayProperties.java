package com.ordertracking.payment.config.razorpay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "razorpay")
public class RazorpayProperties {
    private String keyId;
    private String keySecret;
    private String webhookSecret;
}
