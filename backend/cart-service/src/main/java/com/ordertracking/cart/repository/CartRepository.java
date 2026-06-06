package com.ordertracking.cart.repository;

import com.ordertracking.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
     Optional<Cart> findByCustomerId(Long customerId);
}
