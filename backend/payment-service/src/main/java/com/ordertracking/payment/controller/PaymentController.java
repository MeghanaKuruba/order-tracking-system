package com.ordertracking.payment.controller;

import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentResponse;
import com.ordertracking.payment.dto.PaymentVerificationRequest;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.service.PaymentService;
import com.ordertracking.payment.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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

    @PostMapping("/{razorpayOrderId}/fail")
    public ResponseEntity<Payment> markPaymentAsFailed(
            @PathVariable("razorpayOrderId") String razorpayOrderId,
            @RequestParam(required = false) String reason) {

        Payment payment = paymentService.markPaymentAsFailed(razorpayOrderId, reason);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{orderId}/retry")
    public ResponseEntity<PaymentCheckoutResponse> retryPayment(@PathVariable Long orderId) {
        PaymentCheckoutResponse response = paymentService.retryPayment(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify/retry")
    public ResponseEntity<String> retryVerification(@RequestBody PaymentVerificationRequest request) {
        String response = razorpayService.retryVerification(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload){
        log.info("Webhook payload: {}", payload);


        return ResponseEntity.ok().build();
    }
}
