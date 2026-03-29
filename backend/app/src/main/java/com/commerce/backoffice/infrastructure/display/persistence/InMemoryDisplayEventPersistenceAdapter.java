package com.commerce.backoffice.infrastructure.display.persistence;

import com.commerce.backoffice.application.display.port.out.DisplayEventPersistencePort;
import com.commerce.backoffice.domain.display.DisplayEvent;
import com.commerce.backoffice.domain.display.DisplayEventStatus;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class InMemoryDisplayEventPersistenceAdapter implements DisplayEventPersistencePort {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, DisplayEvent> displayEvents = new ConcurrentHashMap<>();

    @Override
    public DisplayEvent save(
        String name,
        DisplayEventStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Set<Long> productIds
    ) {
        long id = sequence.getAndIncrement();
        DisplayEvent event = DisplayEvent.restore(id, name, status, startAt, endAt, productIds);
        displayEvents.put(id, event);
        return event;
    }

    @Override
    public List<DisplayEvent> findByProductId(long productId) {
        return displayEvents.values()
            .stream()
            .filter(event -> event.productIds().contains(productId))
            .sorted(Comparator.comparing(DisplayEvent::startAt).thenComparing(DisplayEvent::id))
            .toList();
    }
}
