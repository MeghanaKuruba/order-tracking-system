package com.ordertracking.payment.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.dto.RestaurantOrderStatusEvent;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.exception.PaymentNotFoundException;
import com.ordertracking.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundConsumer {

    private final ObjectMapper objectMapper;

    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "restaurant-order-status", groupId = "Payment-group")
    public void consume(String message){
        try{
            RestaurantOrderStatusEvent event = objectMapper.readValue(message, RestaurantOrderStatusEvent.class);
            if (!event.getOrderStatus().equals(("REJECTED"))) {
                return;
            }

            Payment payment = paymentRepository.findByOrderId(event.getOrderId())
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

            if(payment.getPaymentMethod().equals("COD")){
                System.out.println("COD order rejected - no refund needed");
                return;
            }
            payment.setStatus(PaymentStatus.REFUNDED);

            paymentRepository.save(payment);

            System.out.println("Refund processed successfully");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
