package com.commerce.backoffice.application.order.command;

import java.math.BigDecimal;
import java.util.List;

/*
 * 주문 생성 UseCase 입력 모델.
 * - presentation DTO를 application 입력 모델로 분리해서 계층 의존을 분명히 유지한다.
 */
public record CreateOrderCommand(
    Long memberId,
    List<CreateOrderLineCommand> orderLines
) {

    public record CreateOrderLineCommand(
        Long productId,
        int quantity,
        BigDecimal unitPrice
    ) {
    }
}

