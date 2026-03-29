package com.commerce.backoffice.application.display.port.in;

import com.commerce.backoffice.application.display.command.CreateDisplayEventCommand;
import com.commerce.backoffice.application.display.model.ProductExposureResult;
import com.commerce.backoffice.domain.display.DisplayEvent;
import java.time.LocalDateTime;

public interface DisplayEventUseCase {

    DisplayEvent create(CreateDisplayEventCommand command);

    ProductExposureResult getProductExposure(long productId, LocalDateTime evaluatedAt);
}
