package com.commerce.backoffice.interfaces.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record DemoValidationRequest(
    @NotBlank(message = "name은 필수입니다.")
    String name
) {
}

