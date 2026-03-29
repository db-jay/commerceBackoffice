package com.commerce.backoffice.presentation.display.api;

import com.commerce.backoffice.application.display.model.ProductExposureResult;
import com.commerce.backoffice.application.display.port.in.DisplayEventUseCase;
import com.commerce.backoffice.domain.display.DisplayEvent;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
import com.commerce.backoffice.presentation.display.api.dto.CreateDisplayEventRequest;
import com.commerce.backoffice.presentation.display.api.dto.DisplayEventResponse;
import com.commerce.backoffice.presentation.display.api.dto.ProductExposureResponse;
import com.commerce.backoffice.presentation.display.api.mapper.DisplayEventPresentationMapper;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/displays")
public class DisplayEventController {

    private final DisplayEventUseCase displayEventUseCase;
    private final ResponseMapper responseMapper;
    private final DisplayEventPresentationMapper presentationMapper;

    public DisplayEventController(
        DisplayEventUseCase displayEventUseCase,
        ResponseMapper responseMapper,
        DisplayEventPresentationMapper presentationMapper
    ) {
        this.displayEventUseCase = displayEventUseCase;
        this.responseMapper = responseMapper;
        this.presentationMapper = presentationMapper;
    }

    @PostMapping("/events")
    public ResponseEntity<BaseResponse<DisplayEventResponse>> create(
        @Valid @RequestBody CreateDisplayEventRequest request
    ) {
        DisplayEvent displayEvent = displayEventUseCase.create(presentationMapper.toCreateCommand(request));
        return responseMapper.ok(presentationMapper.toResponse(displayEvent));
    }

    @GetMapping("/products/{productId}/exposure")
    public ResponseEntity<BaseResponse<ProductExposureResponse>> getProductExposure(
        @PathVariable long productId,
        @RequestParam("at") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime evaluatedAt
    ) {
        ProductExposureResult result = displayEventUseCase.getProductExposure(productId, evaluatedAt);
        return responseMapper.ok(presentationMapper.toExposureResponse(result));
    }
}
