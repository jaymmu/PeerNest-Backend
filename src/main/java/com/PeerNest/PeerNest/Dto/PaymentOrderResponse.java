package com.PeerNest.PeerNest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentOrderResponse {

    private String orderId;
    private Long courseId;
    private String courseTitle;
    private long amount;
    private String currency;
    private String keyId;
}