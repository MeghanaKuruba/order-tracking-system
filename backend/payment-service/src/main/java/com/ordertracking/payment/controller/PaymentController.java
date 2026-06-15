package com.ordertracking.payment.controller;

import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentResponse;
import com.ordertracking.payment.dto.PaymentVerificationRequest;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.service.PaymentService;
import com.ordertracking.payment.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private final RazorpayService razorpayService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentResponse paymentResponse = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/{paymentId}/checkout")
    public PaymentCheckoutResponse getCheckoutDetails(@PathVariable Long paymentId){
        return razorpayService.getCheckoutDetails(paymentId);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerificationRequest request){
        razorpayService.verifyPayment(request);

        return ResponseEntity.ok("Payment verified successfully");
    }


    @PostMapping("/{paymentId}/fail")
    public ResponseEntity<Payment> markPaymentAsFailed(
            @PathVariable Long paymentId,
            @RequestParam(required = false) String reason) {

        Payment payment = paymentService.markPaymentAsFailed(paymentId, reason);
        return ResponseEntity.ok(payment);
    }

}
