package com.commerce.backoffice.domain.display;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DisplayEventTest {

    @Test
    void isExposed_shouldIncludeBoundaryTimes() {
        DisplayEvent event = DisplayEvent.restore(
            1L,
            "spring-sale",
            DisplayEventStatus.ACTIVE,
            LocalDateTime.of(2026, 3, 30, 0, 0),
            LocalDateTime.of(2026, 3, 31, 23, 59),
            Set.of(100L)
        );

        assertTrue(event.isExposed(100L, LocalDateTime.of(2026, 3, 30, 0, 0)));
        assertTrue(event.isExposed(100L, LocalDateTime.of(2026, 3, 31, 23, 59)));
    }

    @Test
    void isExposed_shouldReturnFalseWhenProductIsNotTargeted() {
        DisplayEvent event = DisplayEvent.restore(
            1L,
            "spring-sale",
            DisplayEventStatus.ACTIVE,
            LocalDateTime.of(2026, 3, 30, 0, 0),
            LocalDateTime.of(2026, 3, 31, 23, 59),
            Set.of(100L)
        );

        assertFalse(event.isExposed(200L, LocalDateTime.of(2026, 3, 30, 12, 0)));
    }
}
