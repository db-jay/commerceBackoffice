package com.commerce.backoffice.presentation.display.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record CreateDisplayEventRequest(
    @NotBlank(message = "이벤트명은 비어 있을 수 없습니다.")
    String name,

    @NotBlank(message = "이벤트 상태는 비어 있을 수 없습니다.")
    String status,

    @NotNull(message = "시작 시간은 필수입니다.")
    LocalDateTime startAt,

    @NotNull(message = "종료 시간은 필수입니다.")
    LocalDateTime endAt,

    @NotEmpty(message = "대상 상품은 1개 이상이어야 합니다.")
    List<Long> productIds
) {
}
