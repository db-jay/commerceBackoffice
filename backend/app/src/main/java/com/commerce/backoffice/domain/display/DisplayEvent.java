package com.commerce.backoffice.domain.display;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/*
 * display/event 컨텍스트의 최소 애그리거트.
 *
 * 책임:
 * - 이벤트 활성 상태를 보관한다.
 * - 기간(startAt/endAt) 안에 있는지 판단한다.
 * - 특정 상품이 전시 대상인지 함께 판단한다.
 */
public class DisplayEvent {

    private final Long id;
    private final String name;
    private final DisplayEventStatus status;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final Set<Long> productIds;

    private DisplayEvent(
        Long id,
        String name,
        DisplayEventStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Set<Long> productIds
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 양수여야 합니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어 있을 수 없습니다.");
        }
        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("기간은 필수입니다.");
        }
        if (startAt.isAfter(endAt)) {
            throw new IllegalArgumentException("startAt은 endAt보다 늦을 수 없습니다.");
        }
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("productIds는 1개 이상이어야 합니다.");
        }

        this.id = id;
        this.name = name;
        this.status = Objects.requireNonNullElse(status, DisplayEventStatus.INACTIVE);
        this.startAt = startAt;
        this.endAt = endAt;
        this.productIds = Set.copyOf(new LinkedHashSet<>(productIds));
    }

    public static DisplayEvent restore(
        Long id,
        String name,
        DisplayEventStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Set<Long> productIds
    ) {
        return new DisplayEvent(id, name, status, startAt, endAt, productIds);
    }

    public boolean isExposed(long productId, LocalDateTime evaluatedAt) {
        if (evaluatedAt == null) {
            throw new IllegalArgumentException("evaluatedAt은 비어 있을 수 없습니다.");
        }

        return status == DisplayEventStatus.ACTIVE
            && productIds.contains(productId)
            && !evaluatedAt.isBefore(startAt)
            && !evaluatedAt.isAfter(endAt);
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public DisplayEventStatus status() {
        return status;
    }

    public LocalDateTime startAt() {
        return startAt;
    }

    public LocalDateTime endAt() {
        return endAt;
    }

    public Set<Long> productIds() {
        return productIds;
    }
}
