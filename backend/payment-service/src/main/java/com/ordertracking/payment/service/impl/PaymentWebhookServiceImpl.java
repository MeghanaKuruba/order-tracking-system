package com.ordertracking.payment.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.dto.PaymentFailureEvent;
import com.ordertracking.payment.dto.PaymentSuccessEvent;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentMethod;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.exception.PaymentNotFoundException;
import com.ordertracking.payment.exception.WebhookProcessingException;
import com.ordertracking.payment.kafka.producer.PaymentEventProducer;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookServiceImpl implements PaymentWebhookService {

    private final ObjectMapper objectMapper;

    private final PaymentRepository paymentRepository;

    private final PaymentEventProducer paymentEventProducer;

    @Override
    public void processWebhook(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.get("event").asText();

            switch (eventType){
                case "payment.captured":

                    handlePaymentCaptured(rootNode);

                    break;

                case "payment.failed":

                    handlePaymentFailed(rootNode);

                    break;

                default:
                    log.info("Ignoring unsupported event: {}", eventType);
            }
        }
        catch (Exception ex){
            log.error("Error processing webhook payload", ex);
            throw new WebhookProcessingException("Webhook processing failed", ex);
        }

    }

    private void handlePaymentCaptured(JsonNode rootNode){
        JsonNode paymentEntity =
                rootNode.path("payload")
                        .path("payment")
                        .path("entity");

        String razorpayOrderId =
                paymentEntity.path("order_id").asText();
        String razorpayPaymentId =
                paymentEntity.path("id").asText();
        String razorpayMethod =
                paymentEntity.path("method").asText();

        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(()-> new PaymentNotFoundException("Payment not found"));

        if(payment.getStatus() == PaymentStatus.SUCCESS){
            log.info("Ignoring duplicate success webhook for payment {}",
                    payment.getPaymentId());

            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);

        payment.setTransactionId(razorpayPaymentId);
        payment.setPaymentMethod(PaymentMethod.valueOf(razorpayMethod.toUpperCase()));

        paymentRepository.save(payment);

        log.info("Payment {} marked SUCCESS", payment.getPaymentId());

        PaymentSuccessEvent event = new PaymentSuccessEvent(
                payment.getOrderId(),
                payment.getPaymentId(),
                payment.getTransactionId(),
                payment.getPaymentMethod().name(),
                payment.getStatus().name(),
                payment.getAmount()
        );

        paymentEventProducer.sendPaymentSuccessEvent(event);
    }

    private void handlePaymentFailed(JsonNode rootNode){
        JsonNode failedPaymentEntity =
                rootNode.path("payload")
                        .path("payment")
                        .path("entity");

        String razorpayOrderId =
                failedPaymentEntity.path("order_id").asText();

        String failureReason = failedPaymentEntity.path("error_description").asText();

        String razorpayPaymentId =
                failedPaymentEntity.path("id").asText();

        String razorpayMethod =
                failedPaymentEntity.path("method").asText();

        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(()-> new PaymentNotFoundException("Payment not found"));

        if(payment.getStatus() == PaymentStatus.SUCCESS){
            log.info("Ignoring failure webhook for successful payment {}",
                    payment.getPaymentId());

            return;
        }

        if (payment.getStatus() == PaymentStatus.FAILED){
            log.info("Ignoring duplicate faliure webhook for payment {}",
                    payment.getPaymentId());
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);

        payment.setFailureReason(failureReason);

        payment.setTransactionId(razorpayPaymentId);

        if (!razorpayMethod.isBlank()) {
            payment.setPaymentMethod(
                    PaymentMethod.valueOf(razorpayMethod.toUpperCase())
            );
        } else {
            payment.setPaymentMethod(PaymentMethod.UNKNOWN);
        }

        paymentRepository.save(payment);

        log.info("Payment {} marked FAILED", payment.getPaymentId());

        PaymentFailureEvent event = new PaymentFailureEvent(
                payment.getOrderId(),
                payment.getPaymentId(),
                payment.getStatus().name(),
                payment.getFailureReason(),
                payment.getAmount()
        );

        paymentEventProducer.sendPaymentFailureEvent(event);
    }
}
