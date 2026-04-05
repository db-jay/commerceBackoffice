package com.commerce.backoffice.presentation.delivery.api;

import com.commerce.backoffice.application.delivery.port.in.DeliveryUseCase;
import com.commerce.backoffice.domain.delivery.Delivery;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
import com.commerce.backoffice.presentation.delivery.api.dto.ChangeDeliveryStatusRequest;
import com.commerce.backoffice.presentation.delivery.api.dto.CreateDeliveryRequest;
import com.commerce.backoffice.presentation.delivery.api.dto.DeliveryResponse;
import com.commerce.backoffice.presentation.delivery.api.dto.RegisterTrackingNumberRequest;
import com.commerce.backoffice.presentation.delivery.api.mapper.DeliveryPresentationMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryUseCase deliveryUseCase;
    private final ResponseMapper responseMapper;
    private final DeliveryPresentationMapper presentationMapper;

    public DeliveryController(
        DeliveryUseCase deliveryUseCase,
        ResponseMapper responseMapper,
        DeliveryPresentationMapper presentationMapper
    ) {
        this.deliveryUseCase = deliveryUseCase;
        this.responseMapper = responseMapper;
        this.presentationMapper = presentationMapper;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<DeliveryResponse>> create(
        @Valid @RequestBody CreateDeliveryRequest request
    ) {
        Delivery delivery = deliveryUseCase.create(presentationMapper.toCreateCommand(request));
        return responseMapper.ok(presentationMapper.toResponse(delivery));
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<BaseResponse<DeliveryResponse>> getById(@PathVariable long deliveryId) {
        Delivery delivery = deliveryUseCase.getById(deliveryId);
        return responseMapper.ok(presentationMapper.toResponse(delivery));
    }

    @PatchMapping("/{deliveryId}/tracking-number")
    public ResponseEntity<BaseResponse<DeliveryResponse>> registerTrackingNumber(
        @PathVariable long deliveryId,
        @Valid @RequestBody RegisterTrackingNumberRequest request
    ) {
        Delivery delivery = deliveryUseCase.registerTrackingNumber(
            deliveryId,
            presentationMapper.toRegisterTrackingNumberCommand(request)
        );
        return responseMapper.ok(presentationMapper.toResponse(delivery));
    }

    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<BaseResponse<DeliveryResponse>> changeStatus(
        @PathVariable long deliveryId,
        @Valid @RequestBody ChangeDeliveryStatusRequest request
    ) {
        Delivery delivery = deliveryUseCase.changeStatus(
            deliveryId,
            presentationMapper.toChangeStatusCommand(request)
        );
        return responseMapper.ok(presentationMapper.toResponse(delivery));
    }
}
