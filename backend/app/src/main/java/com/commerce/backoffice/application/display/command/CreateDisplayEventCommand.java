package com.commerce.backoffice.application.display.command;

import java.time.LocalDateTime;
import java.util.List;

public record CreateDisplayEventCommand(
    String name,
    String status,
    LocalDateTime startAt,
    LocalDateTime endAt,
    List<Long> productIds
) {
}
