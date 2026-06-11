package com.ordertracking.payment.service;

import com.razorpay.RazorpayClient;

import java.math.BigDecimal;

public interface RazorpayService {

    String createRazorpayOrder(Long paymentId, BigDecimal amount);
}
