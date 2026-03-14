package com.commerce.backoffice.presentation.catalog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/*
 * 상품 상태 변경 요청 DTO.
 */
public record ChangeCatalogProductStatusRequest(
    @NotBlank(message = "status는 필수입니다.")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "status는 ACTIVE 또는 INACTIVE여야 합니다.")
    String status
) {
}

