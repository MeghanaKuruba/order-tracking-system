package com.ordertracking.order.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.order.dto.RestaurantOrderStatusEvent;
import com.ordertracking.order.entity.Order;
import com.ordertracking.order.entity.OrderStatus;
import com.ordertracking.order.exception.OrderNotFoundException;
import com.ordertracking.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantOrderStatusConsumer {

    private final ObjectMapper objectMapper;

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "restaurant-order-status", groupId = "order-group")
    public void consume(String message){
        try{
            RestaurantOrderStatusEvent event = objectMapper.readValue(message, RestaurantOrderStatusEvent.class);

            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException("Order not found"));

            order.setStatus(OrderStatus.valueOf(event.getOrderStatus()));

            orderRepository.save(order);

            System.out.println("Order status updated");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
