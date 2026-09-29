package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.PaymentOrderResponse;
import com.PeerNest.PeerNest.Dto.PaymentVerificationRequest;
import com.PeerNest.PeerNest.Dto.PaymentVerificationResponse;
import com.PeerNest.PeerNest.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order/{courseId}")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @PathVariable Long courseId,
            Authentication authentication
    ) {

        PaymentOrderResponse response =
                paymentService.createOrder(
                        courseId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request,
            Authentication authentication
    ) {

        PaymentVerificationResponse response =
                paymentService.verifyPayment(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}