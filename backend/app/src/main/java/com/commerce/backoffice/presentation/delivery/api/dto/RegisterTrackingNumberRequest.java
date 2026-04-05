package com.commerce.backoffice.presentation.delivery.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterTrackingNumberRequest(
    @NotBlank(message = "trackingNumber는 비어 있을 수 없습니다.")
    String trackingNumber
) {
}
