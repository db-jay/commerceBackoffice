package com.commerce.backoffice.application.delivery.service;

import com.commerce.backoffice.application.delivery.command.ChangeDeliveryStatusCommand;
import com.commerce.backoffice.application.delivery.command.CreateDeliveryCommand;
import com.commerce.backoffice.application.delivery.command.RegisterTrackingNumberCommand;
import com.commerce.backoffice.application.delivery.port.in.DeliveryUseCase;
import com.commerce.backoffice.application.delivery.port.out.DeliveryPersistencePort;
import com.commerce.backoffice.application.order.port.out.OrderPersistencePort;
import com.commerce.backoffice.domain.delivery.Delivery;
import com.commerce.backoffice.domain.delivery.DeliveryStatus;
import com.commerce.backoffice.domain.exception.BusinessException;
import com.commerce.backoffice.domain.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryApplicationService implements DeliveryUseCase {

    private final DeliveryPersistencePort deliveryPersistencePort;
    private final OrderPersistencePort orderPersistencePort;

    public DeliveryApplicationService(
        DeliveryPersistencePort deliveryPersistencePort,
        OrderPersistencePort orderPersistencePort
    ) {
        this.deliveryPersistencePort = deliveryPersistencePort;
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    @Transactional
    public Delivery create(CreateDeliveryCommand command) {
        orderPersistencePort.findById(command.orderId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        return deliveryPersistencePort.save(command.orderId());
    }

    @Override
    @Transactional(readOnly = true)
    public Delivery getById(long deliveryId) {
        return loadDelivery(deliveryId);
    }

    @Override
    @Transactional
    public Delivery registerTrackingNumber(long deliveryId, RegisterTrackingNumberCommand command) {
        Delivery delivery = loadDelivery(deliveryId);

        try {
            delivery.registerTrackingNumber(command.trackingNumber());
        } catch (IllegalStateException ex) {
            throw new BusinessException(ErrorCode.DELIVERY_TRACKING_NUMBER_NOT_ALLOWED);
        }

        deliveryPersistencePort.update(delivery);
        return delivery;
    }

    @Override
    @Transactional
    public Delivery changeStatus(long deliveryId, ChangeDeliveryStatusCommand command) {
        Delivery delivery = loadDelivery(deliveryId);
        DeliveryStatus targetStatus = DeliveryStatus.valueOf(command.status());

        try {
            switch (targetStatus) {
                case SHIPPED -> delivery.markShipped();
                case IN_DELIVERY -> delivery.startDelivery();
                case DELIVERED -> delivery.completeDelivery();
                case READY -> throw new BusinessException(ErrorCode.DELIVERY_INVALID_STATUS_TRANSITION);
            }
        } catch (IllegalStateException ex) {
            if (targetStatus == DeliveryStatus.SHIPPED) {
                throw new BusinessException(ErrorCode.DELIVERY_TRACKING_NUMBER_REQUIRED);
            }
            throw new BusinessException(ErrorCode.DELIVERY_INVALID_STATUS_TRANSITION);
        }

        deliveryPersistencePort.update(delivery);
        return delivery;
    }

    private Delivery loadDelivery(long deliveryId) {
        return deliveryPersistencePort.findById(deliveryId)
            .orElseThrow(() -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND));
    }
}
