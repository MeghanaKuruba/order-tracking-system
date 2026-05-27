package com.ordertracking.payment.kafka;

import com.ordertracking.payment.dto.OrderCreatedEvent;
import com.ordertracking.payment.dto.PaymentSuccessEvent;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;

    private final PaymentRepository paymentRepository;

    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void consume(String message) {
        try{
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            Payment payment = new Payment();
            payment.setOrderId(event.getOrderId());
            payment.setCustomerId(event.getCustomerId());
            payment.setAmount(event.getTotalAmount());
            payment.setPaymentMethod("UPI");
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setCreatedAt(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);

            PaymentSuccessEvent successEvent = new PaymentSuccessEvent(
                    savedPayment.getOrderId(),
                    savedPayment.getPaymentId(),
                    savedPayment.getTransactionId(),
                    savedPayment.getStatus().name(),
                    savedPayment.getAmount()
            );
            paymentEventProducer.sendPaymentSuccessEvent(successEvent);

            System.out.println("Processed payment for order ID: " + event.getOrderId());
        } catch (Exception e) {
            System.out.println("Failed to process payment event: " + e.getMessage());
        }
    }
}
