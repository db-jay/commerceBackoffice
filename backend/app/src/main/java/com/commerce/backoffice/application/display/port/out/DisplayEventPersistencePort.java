package com.commerce.backoffice.application.display.port.out;

import com.commerce.backoffice.domain.display.DisplayEvent;
import com.commerce.backoffice.domain.display.DisplayEventStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface DisplayEventPersistencePort {

    DisplayEvent save(
        String name,
        DisplayEventStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Set<Long> productIds
    );

    List<DisplayEvent> findByProductId(long productId);
}
