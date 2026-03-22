package com.commerce.backoffice.presentation.order.api;

import com.commerce.backoffice.application.order.port.in.OrderUseCase;
import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
import com.commerce.backoffice.presentation.order.api.dto.CreateOrderRequest;
import com.commerce.backoffice.presentation.order.api.dto.OrderResponse;
import com.commerce.backoffice.presentation.order.api.mapper.OrderPresentationMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Order API Controller.
 *
 * 역할:
 * - HTTP 요청/응답 처리
 * - 요청 검증(@Valid)
 * - UseCase 호출
 *
 * 변환 로직(dto <-> command/response)은 OrderPresentationMapper로 분리한다.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;
    private final ResponseMapper responseMapper;
    private final OrderPresentationMapper presentationMapper;

    public OrderController(
        OrderUseCase orderUseCase,
        ResponseMapper responseMapper,
        OrderPresentationMapper presentationMapper
    ) {
        this.orderUseCase = orderUseCase;
        this.responseMapper = responseMapper;
        this.presentationMapper = presentationMapper;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<OrderResponse>> create(
        @Valid @RequestBody CreateOrderRequest request
    ) {
        Order order = orderUseCase.create(presentationMapper.toCreateCommand(request));
        return responseMapper.ok(presentationMapper.toResponse(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse<OrderResponse>> getById(@PathVariable long orderId) {
        Order order = orderUseCase.getById(orderId);
        return responseMapper.ok(presentationMapper.toResponse(order));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<BaseResponse<OrderResponse>> cancel(@PathVariable long orderId) {
        Order order = orderUseCase.cancel(orderId);
        return responseMapper.ok(presentationMapper.toResponse(order));
    }
}
