package com.commerce.backoffice.application.display.model;

import java.time.LocalDateTime;

public record ProductExposureResult(
    long productId,
    boolean exposed,
    LocalDateTime evaluatedAt,
    Long eventId,
    String eventName
) {
    public static ProductExposureResult exposed(long productId, LocalDateTime evaluatedAt, Long eventId, String eventName) {
        return new ProductExposureResult(productId, true, evaluatedAt, eventId, eventName);
    }

    public static ProductExposureResult hidden(long productId, LocalDateTime evaluatedAt) {
        return new ProductExposureResult(productId, false, evaluatedAt, null, null);
    }
}
