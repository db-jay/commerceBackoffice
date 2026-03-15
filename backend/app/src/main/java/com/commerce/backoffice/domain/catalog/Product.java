package com.commerce.backoffice.domain.catalog;

import java.math.BigDecimal;
import java.util.Objects;

/*
 * [역할]
 * - 상품의 기본 정보(이름/가격), 재고 수량, 상태를 함께 관리하는 도메인 객체다.
 *
 * [왜 필요한가]
 * - "재고가 충분한가?", "재고를 줄여도 되는가?" 같은 규칙은
 *   DB나 Controller가 아니라 상품 자체가 판단해야 규칙 위치가 흔들리지 않는다.
 *
 * [흐름]
 * - Application Service가 Product를 조회한다.
 * - Product가 reserveStock()/releaseStock() 같은 규칙을 수행한다.
 * - 그 결과를 Infrastructure가 DB에 반영한다.
 *
 * [주의할 점]
 * - version은 낙관적 락을 위한 값이다.
 * - version이 다르면 "누군가 먼저 수정했다"는 뜻이므로 저장 단계에서 충돌로 처리한다.
 * - Domain은 Spring/JPA 없이 순수 자바 코드로 유지한다.
 */
public class Product {

    private final Long id;
    private final long version;
    private String name;
    private BigDecimal price;
    private int stockQuantity;
    private ProductStatus status;

    public Product(Long id, String name, BigDecimal price, int stockQuantity, ProductStatus status) {
        this(id, name, price, stockQuantity, status, 0L);
    }

    public Product(Long id, String name, BigDecimal price, int stockQuantity, ProductStatus status, long version) {
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
        if (version < 0) {
            throw new IllegalArgumentException("version은 0 이상이어야 합니다.");
        }
        this.id = id;
        this.version = version;
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

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("newName은 비어 있을 수 없습니다.");
        }
        this.name = newName;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
    }

    public Long id() {
        return id;
    }

    public long version() {
        return version;
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
