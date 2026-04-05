package com.commerce.backoffice.application.delivery.port.in;

import com.commerce.backoffice.application.delivery.command.ChangeDeliveryStatusCommand;
import com.commerce.backoffice.application.delivery.command.CreateDeliveryCommand;
import com.commerce.backoffice.application.delivery.command.RegisterTrackingNumberCommand;
import com.commerce.backoffice.domain.delivery.Delivery;

public interface DeliveryUseCase {

    Delivery create(CreateDeliveryCommand command);

    Delivery getById(long deliveryId);

    Delivery registerTrackingNumber(long deliveryId, RegisterTrackingNumberCommand command);

    Delivery changeStatus(long deliveryId, ChangeDeliveryStatusCommand command);
}
