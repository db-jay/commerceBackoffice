package com.commerce.backoffice.presentation.delivery.api.dto;

import jakarta.validation.constraints.Positive;

public record CreateDeliveryRequest(
    @Positive(message = "orderId는 양수여야 합니다.")
    long orderId
) {
}
