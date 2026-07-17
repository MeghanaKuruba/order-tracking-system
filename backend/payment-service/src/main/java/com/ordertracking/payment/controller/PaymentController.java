package com.ordertracking.payment.controller;

import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentHistoryResponse;
import com.ordertracking.payment.dto.PaymentVerificationRequest;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.service.PaymentService;
import com.ordertracking.payment.service.PaymentWebhookService;
import com.ordertracking.payment.service.RazorpayService;
import com.ordertracking.payment.service.WebhookValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private final RazorpayService razorpayService;

    private final WebhookValidationService webhookValidationService;

    private final PaymentWebhookService paymentWebhookService;

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
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature")
            String signature) {

        webhookValidationService.validate(payload, signature);

        paymentWebhookService.processWebhook(payload);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentHistoryResponse> getPaymentHistoryByOrderId(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.getPaymentHistoryByOrderId(orderId));

    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<PaymentHistoryResponse>> getCustomerPaymentHistory(

            @PathVariable Long customerId,

            @RequestParam(required = false)
            PaymentStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(

                paymentService.getCustomerPaymentHistory(

                        customerId,
                        status,
                        page,
                        size));

    }
}
