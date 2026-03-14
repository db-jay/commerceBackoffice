package com.commerce.backoffice.presentation.order.api.mapper;

import com.commerce.backoffice.application.order.command.CreateOrderCommand;
import com.commerce.backoffice.application.order.command.CreateOrderCommand.CreateOrderLineCommand;
import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.domain.order.OrderLine;
import com.commerce.backoffice.presentation.order.api.dto.CreateOrderRequest;
import com.commerce.backoffice.presentation.order.api.dto.OrderResponse;
import com.commerce.backoffice.presentation.order.api.dto.OrderResponse.OrderLineResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/*
 * Order 프레젠테이션 매퍼 구현체.
 * - 중첩 리스트(주문 라인) 변환을 Controller 밖으로 이동시켜
 *   Controller 메서드가 한눈에 읽히도록 만든다.
 */
@Component
public class OrderPresentationMapperImpl implements OrderPresentationMapper {

    @Override
    public CreateOrderCommand toCreateCommand(CreateOrderRequest request) {
        List<CreateOrderLineCommand> lines = request.orderLines()
            .stream()
            .map(line -> new CreateOrderLineCommand(
                line.productId(),
                line.quantity(),
                line.unitPrice()
            ))
            .toList();

        return new CreateOrderCommand(request.memberId(), lines);
    }

    @Override
    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.id(),
            order.memberId(),
            order.status().name(),
            order.totalAmount(),
            toLineResponses(order.orderLines())
        );
    }

    private List<OrderLineResponse> toLineResponses(List<OrderLine> orderLines) {
        return orderLines.stream()
            .map(line -> new OrderLineResponse(
                line.productId(),
                line.quantity(),
                line.unitPrice(),
                line.lineAmount()
            ))
            .toList();
    }
}
