package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentOrderRepository
        extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByRazorpayOrderId(
            String razorpayOrderId
    );

    boolean existsByRazorpayOrderId(
            String razorpayOrderId
    );
}