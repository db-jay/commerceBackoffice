package com.commerce.backoffice.presentation.display.api.mapper;

import com.commerce.backoffice.application.display.command.CreateDisplayEventCommand;
import com.commerce.backoffice.application.display.model.ProductExposureResult;
import com.commerce.backoffice.domain.display.DisplayEvent;
import com.commerce.backoffice.presentation.display.api.dto.CreateDisplayEventRequest;
import com.commerce.backoffice.presentation.display.api.dto.DisplayEventResponse;
import com.commerce.backoffice.presentation.display.api.dto.ProductExposureResponse;

public interface DisplayEventPresentationMapper {

    CreateDisplayEventCommand toCreateCommand(CreateDisplayEventRequest request);

    DisplayEventResponse toResponse(DisplayEvent displayEvent);

    ProductExposureResponse toExposureResponse(ProductExposureResult result);
}
