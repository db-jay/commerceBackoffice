package com.commerce.backoffice.domain.delivery;

/*
 * Delivery 컨텍스트의 최소 애그리거트.
 *
 * 책임:
 * - order 이후 fulfillment 상태를 보관한다.
 * - 송장 번호 등록 가능 시점과 배송 상태 전이 순서를 강제한다.
 */
public class Delivery {

    private final Long id;
    private final Long orderId;
    private DeliveryStatus status;
    private String trackingNumber;

    private Delivery(Long id, Long orderId) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 양수여야 합니다.");
        }
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("orderId는 양수여야 합니다.");
        }
        this.id = id;
        this.orderId = orderId;
        this.status = DeliveryStatus.READY;
    }

    public static Delivery create(Long id, Long orderId) {
        return new Delivery(id, orderId);
    }

    public static Delivery restore(Long id, Long orderId, DeliveryStatus status, String trackingNumber) {
        Delivery delivery = new Delivery(id, orderId);
        delivery.status = status == null ? DeliveryStatus.READY : status;
        delivery.trackingNumber = (trackingNumber == null || trackingNumber.isBlank()) ? null : trackingNumber;
        return delivery;
    }

    public void registerTrackingNumber(String newTrackingNumber) {
        if (status != DeliveryStatus.READY) {
            throw new IllegalStateException("READY 상태에서만 송장 번호를 등록할 수 있습니다.");
        }
        if (newTrackingNumber == null || newTrackingNumber.isBlank()) {
            throw new IllegalArgumentException("trackingNumber는 비어 있을 수 없습니다.");
        }
        this.trackingNumber = newTrackingNumber;
    }

    public void markShipped() {
        if (status != DeliveryStatus.READY) {
            throw new IllegalStateException("READY 상태에서만 출고할 수 있습니다.");
        }
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalStateException("송장 번호 없이 출고할 수 없습니다.");
        }
        this.status = DeliveryStatus.SHIPPED;
    }

    public void startDelivery() {
        if (status != DeliveryStatus.SHIPPED) {
            throw new IllegalStateException("SHIPPED 상태에서만 배송 중으로 전이할 수 있습니다.");
        }
        this.status = DeliveryStatus.IN_DELIVERY;
    }

    public void completeDelivery() {
        if (status != DeliveryStatus.IN_DELIVERY) {
            throw new IllegalStateException("IN_DELIVERY 상태에서만 배송 완료로 전이할 수 있습니다.");
        }
        this.status = DeliveryStatus.DELIVERED;
    }

    public Long id() {
        return id;
    }

    public Long orderId() {
        return orderId;
    }

    public DeliveryStatus status() {
        return status;
    }

    public String trackingNumber() {
        return trackingNumber;
    }
}
