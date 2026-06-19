package com.ordertracking.payment.service;

public interface PaymentWebhookService {

    void processWebhook(String payload);
}
