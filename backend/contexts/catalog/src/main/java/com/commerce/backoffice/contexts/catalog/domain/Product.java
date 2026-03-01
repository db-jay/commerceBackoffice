package com.commerce.backoffice.contexts.catalog.domain;

import java.math.BigDecimal;
import java.util.Objects;

/*
 * Catalog 컨텍스트의 상품 애그리거트 초안.
 * - 가격, 재고, 상태를 한 객체에서 일관되게 관리한다.
 * - Spring/JPA 의존 없이 순수 비즈니스 규칙만 담는다.
 */
public class Product {

    private final Long id;
    private final String name;
    private BigDecimal price;
    private int stockQuantity;
    private ProductStatus status;

    public Product(Long id, String name, BigDecimal price, int stockQuantity, ProductStatus status) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 양수여야 합니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어 있을 수 없습니다.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price는 0 이상이어야 합니다.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("stockQuantity는 0 이상이어야 합니다.");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = Objects.requireNonNullElse(status, ProductStatus.ACTIVE);
    }

    public void reserveStock(int quantity) {
        /*
         * 주문 시 재고 예약.
         * 재고보다 많은 수량을 예약하려고 하면 즉시 예외를 던진다.
         */
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stockQuantity -= quantity;
    }

    public void releaseStock(int quantity) {
        // 주문 취소/반품 시 재고를 되돌리는 용도.
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
        this.stockQuantity += quantity;
    }

    public void changePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("newPrice는 0 이상이어야 합니다.");
        }
        this.price = newPrice;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public BigDecimal price() {
        return price;
    }

    public int stockQuantity() {
        return stockQuantity;
    }

    public ProductStatus status() {
        return status;
    }
}

