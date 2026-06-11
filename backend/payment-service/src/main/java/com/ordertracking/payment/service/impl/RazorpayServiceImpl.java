package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.exception.FailedRazorpayOrderCreation;
import com.ordertracking.payment.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;

    @Override
    public String createRazorpayOrder(Long paymentId, BigDecimal amount) {
        try {
            JSONObject options = new JSONObject();
            options.put("amount", amount.multiply(BigDecimal.valueOf(100)));
            options.put("currency", "INR");
            options.put("receipt", "payment_"+paymentId);

            Order razorpayOrder = razorpayClient.orders.create(options);

            return razorpayOrder.get("id");

        } catch (RazorpayException e) {
            throw new FailedRazorpayOrderCreation("Failed to create Razorpay order");
        }
    }
}
