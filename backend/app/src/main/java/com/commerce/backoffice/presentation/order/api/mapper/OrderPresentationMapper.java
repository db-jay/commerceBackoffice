package com.commerce.backoffice.presentation.order.api.mapper;

import com.commerce.backoffice.application.order.command.CreateOrderCommand;
import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.presentation.order.api.dto.CreateOrderRequest;
import com.commerce.backoffice.presentation.order.api.dto.OrderResponse;

/*
 * Order 프레젠테이션 매퍼 계약.
 *
 * Controller에서 request/response 변환 코드를 걷어내서
 * 주문 유스케이스 호출 흐름이 더 잘 읽히도록 분리한다.
 */
public interface OrderPresentationMapper {

    CreateOrderCommand toCreateCommand(CreateOrderRequest request);

    OrderResponse toResponse(Order order);
}
