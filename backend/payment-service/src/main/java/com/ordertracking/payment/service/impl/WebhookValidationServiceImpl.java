package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.config.razorpay.RazorpayProperties;
import com.ordertracking.payment.service.WebhookValidationService;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ordertracking.payment.exception.InvalidWebhookSignatureException;

@Service
@RequiredArgsConstructor
public class WebhookValidationServiceImpl
        implements WebhookValidationService {

    private final RazorpayProperties razorpayProperties;

    @Override
    public void validate(String payload, String signature) {

        try {

            Utils.verifyWebhookSignature(
                    payload,
                    signature,
                    razorpayProperties.getWebhookSecret()
            );

        } catch (Exception ex) {

            throw new InvalidWebhookSignatureException(
                    "Invalid Razorpay webhook signature"
            );
        }
    }
}