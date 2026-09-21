package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.PaymentOrderResponse;
import com.PeerNest.PeerNest.Dto.PaymentVerificationRequest;
import com.PeerNest.PeerNest.Dto.PaymentVerificationResponse;

public interface PaymentService {

    PaymentOrderResponse createOrder(
            Long courseId,
            String studentEmail
    );

    PaymentVerificationResponse verifyPayment(
            PaymentVerificationRequest request,
            String studentEmail
    );
}