package com.ordertracking.payment.service;

public interface WebhookValidationService {

    void validate(String payload, String signature);

}