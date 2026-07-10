package com.ordertracking.payment.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.dto.OrderCreatedEvent;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.kafka.producer.PaymentEventProducer;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;

    private final PaymentRepository paymentRepository;

    private final RazorpayService razorpayService;

    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void consume(String message) {

        OrderCreatedEvent event = null;
        try{
            event = objectMapper.readValue(message, OrderCreatedEvent.class);
            Payment payment = new Payment();
            payment.setOrderId(event.getOrderId());
            payment.setCustomerId(event.getCustomerId());
            payment.setAmount(event.getTotalAmount());
            payment.setAttemptNumber(1);
            payment.setStatus(PaymentStatus.PENDING_PAYMENT);
            payment.setCreatedAt(LocalDateTime.now());

            Payment savedPayment = paymentRepository.save(payment);

            String razorpayOrderId = razorpayService.createRazorpayOrder(
                    savedPayment.getOrderId(),
                    savedPayment.getAmount()
            );

            savedPayment.setRazorpayOrderId(razorpayOrderId);

            paymentRepository.save(savedPayment);

            log.info("Payment created for order {}", event.getOrderId());

        }
        catch (DataIntegrityViolationException ex) {

            log.info(
                    "Duplicate Kafka event ignored. Payment already exists for orderId={}",
                    event.getOrderId()
            );

        }
        catch (Exception ex) {

            log.error("Failed to process order-created event", ex);

        }
    }
}
