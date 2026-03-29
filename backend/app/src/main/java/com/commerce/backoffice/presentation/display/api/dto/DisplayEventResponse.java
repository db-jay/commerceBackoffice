package com.commerce.backoffice.presentation.display.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayEventResponse(
    Long id,
    String name,
    String status,
    LocalDateTime startAt,
    LocalDateTime endAt,
    List<Long> productIds
) {
}
