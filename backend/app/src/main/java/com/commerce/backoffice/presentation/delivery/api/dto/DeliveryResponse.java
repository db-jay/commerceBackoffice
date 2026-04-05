package com.commerce.backoffice.presentation.delivery.api.dto;

public record DeliveryResponse(
    Long id,
    Long orderId,
    String status,
    String trackingNumber
) {
}
