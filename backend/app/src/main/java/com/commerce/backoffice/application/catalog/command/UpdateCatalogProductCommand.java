package com.commerce.backoffice.application.catalog.command;

import java.math.BigDecimal;

/*
 * 상품 수정 입력 커맨드.
 * - 이름/가격 같은 기본 정보를 수정할 때 사용한다.
 */
public record UpdateCatalogProductCommand(
    String name,
    BigDecimal price
) {
}

