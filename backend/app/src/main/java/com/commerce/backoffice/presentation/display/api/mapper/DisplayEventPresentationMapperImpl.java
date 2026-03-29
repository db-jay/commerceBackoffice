package com.commerce.backoffice.presentation.display.api.mapper;

import com.commerce.backoffice.application.display.command.CreateDisplayEventCommand;
import com.commerce.backoffice.application.display.model.ProductExposureResult;
import com.commerce.backoffice.domain.display.DisplayEvent;
import com.commerce.backoffice.presentation.display.api.dto.CreateDisplayEventRequest;
import com.commerce.backoffice.presentation.display.api.dto.DisplayEventResponse;
import com.commerce.backoffice.presentation.display.api.dto.ProductExposureResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayEventPresentationMapperImpl implements DisplayEventPresentationMapper {

    @Override
    public CreateDisplayEventCommand toCreateCommand(CreateDisplayEventRequest request) {
        return new CreateDisplayEventCommand(
            request.name(),
            request.status(),
            request.startAt(),
            request.endAt(),
            request.productIds()
        );
    }

    @Override
    public DisplayEventResponse toResponse(DisplayEvent displayEvent) {
        return new DisplayEventResponse(
            displayEvent.id(),
            displayEvent.name(),
            displayEvent.status().name(),
            displayEvent.startAt(),
            displayEvent.endAt(),
            displayEvent.productIds().stream().toList()
        );
    }

    @Override
    public ProductExposureResponse toExposureResponse(ProductExposureResult result) {
        return new ProductExposureResponse(
            result.productId(),
            result.exposed(),
            result.evaluatedAt(),
            result.eventId(),
            result.eventName()
        );
    }
}
