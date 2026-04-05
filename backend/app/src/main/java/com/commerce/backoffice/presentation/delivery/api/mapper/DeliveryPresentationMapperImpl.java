package com.commerce.backoffice.presentation.delivery.api.mapper;

import com.commerce.backoffice.application.delivery.command.ChangeDeliveryStatusCommand;
import com.commerce.backoffice.application.delivery.command.CreateDeliveryCommand;
import com.commerce.backoffice.application.delivery.command.RegisterTrackingNumberCommand;
import com.commerce.backoffice.domain.delivery.Delivery;
import com.commerce.backoffice.presentation.delivery.api.dto.ChangeDeliveryStatusRequest;
import com.commerce.backoffice.presentation.delivery.api.dto.CreateDeliveryRequest;
import com.commerce.backoffice.presentation.delivery.api.dto.DeliveryResponse;
import com.commerce.backoffice.presentation.delivery.api.dto.RegisterTrackingNumberRequest;
import org.springframework.stereotype.Component;

@Component
public class DeliveryPresentationMapperImpl implements DeliveryPresentationMapper {

    @Override
    public CreateDeliveryCommand toCreateCommand(CreateDeliveryRequest request) {
        return new CreateDeliveryCommand(request.orderId());
    }

    @Override
    public RegisterTrackingNumberCommand toRegisterTrackingNumberCommand(RegisterTrackingNumberRequest request) {
        return new RegisterTrackingNumberCommand(request.trackingNumber());
    }

    @Override
    public ChangeDeliveryStatusCommand toChangeStatusCommand(ChangeDeliveryStatusRequest request) {
        return new ChangeDeliveryStatusCommand(request.status());
    }

    @Override
    public DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
            delivery.id(),
            delivery.orderId(),
            delivery.status().name(),
            delivery.trackingNumber()
        );
    }
}
