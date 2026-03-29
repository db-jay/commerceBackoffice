package com.commerce.backoffice.presentation.display.api.dto;

import java.time.LocalDateTime;

public record ProductExposureResponse(
    long productId,
    boolean exposed,
    LocalDateTime evaluatedAt,
    Long eventId,
    String eventName
) {
}
