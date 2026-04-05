package com.commerce.backoffice.presentation.delivery.api.mapper;

import com.commerce.backoffice.application.delivery.command.ChangeDeliveryStatusCommand;
import com.commerce.backoffice.application.delivery.command.CreateDeliveryCommand;
import com.commerce.backoffice.application.delivery.command.RegisterTrackingNumberCommand;
import com.commerce.backoffice.domain.delivery.Delivery;
import com.commerce.backoffice.presentation.delivery.api.dto.ChangeDeliveryStatusRequest;
import com.commerce.backoffice.presentation.delivery.api.dto.CreateDeliveryRequest;
import com.commerce.backoffice.presentation.delivery.api.dto.DeliveryResponse;
import com.commerce.backoffice.presentation.delivery.api.dto.RegisterTrackingNumberRequest;

public interface DeliveryPresentationMapper {

    CreateDeliveryCommand toCreateCommand(CreateDeliveryRequest request);

    RegisterTrackingNumberCommand toRegisterTrackingNumberCommand(RegisterTrackingNumberRequest request);

    ChangeDeliveryStatusCommand toChangeStatusCommand(ChangeDeliveryStatusRequest request);

    DeliveryResponse toResponse(Delivery delivery);
}
