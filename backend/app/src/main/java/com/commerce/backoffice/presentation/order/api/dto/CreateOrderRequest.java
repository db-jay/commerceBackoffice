package com.commerce.backoffice.presentation.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/*
 * 주문 생성 요청 DTO.
 */
public record CreateOrderRequest(
    @NotNull(message = "memberId는 필수입니다.")
    @Positive(message = "memberId는 양수여야 합니다.")
    Long memberId,

    @NotEmpty(message = "orderLines는 1개 이상이어야 합니다.")
    List<@Valid OrderLineRequest> orderLines
) {
    public record OrderLineRequest(
        @NotNull(message = "productId는 필수입니다.")
        @Positive(message = "productId는 양수여야 합니다.")
        Long productId,

        @Positive(message = "quantity는 1 이상이어야 합니다.")
        int quantity,

        @NotNull(message = "unitPrice는 필수입니다.")
        @DecimalMin(value = "0", message = "unitPrice는 0 이상이어야 합니다.")
        BigDecimal unitPrice
    ) {
    }
}

