package com.commerce.backoffice.contexts.catalog.application.command;

import java.math.BigDecimal;

/*
 * UseCase 입력 전용 커맨드 객체.
 *
 * 왜 DTO를 직접 넘기지 않나?
 * - interfaces 계층(HTTP) 모델과 application 계층 모델을 분리하기 위해서다.
 * - 나중에 REST가 아닌 다른 입력 채널(배치/메시지)로 바뀌어도 UseCase 시그니처를 유지할 수 있다.
 */
public record CreateCatalogProductCommand(
    String name,
    BigDecimal price,
    int stockQuantity
) {
}
