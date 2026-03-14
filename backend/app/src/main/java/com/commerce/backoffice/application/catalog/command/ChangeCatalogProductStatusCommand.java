package com.commerce.backoffice.application.catalog.command;

/*
 * 상품 상태 변경 입력 커맨드.
 */
public record ChangeCatalogProductStatusCommand(
    String status
) {
}

