package com.PeerNest.PeerNest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentVerificationResponse {

    private boolean success;
    private String message;
    private Long enrollmentId;
}