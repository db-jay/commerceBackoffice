package com.commerce.backoffice.interfaces.demo;

import java.util.Map;
import com.commerce.backoffice.application.demo.DemoApplicationService;
import com.commerce.backoffice.interfaces.common.response.BaseResponse;
import com.commerce.backoffice.interfaces.common.response.ResponseMapper;
import com.commerce.backoffice.interfaces.demo.dto.DemoValidationRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {
    /*
     * 공통 예외 처리/응답 구조를 검증하기 위한 데모 컨트롤러.
     * 운영 기능이 아니라 학습/테스트 목적의 엔드포인트다.
     */

    private final DemoApplicationService demoApplicationService;
    private final ResponseMapper responseMapper;

    public DemoController(DemoApplicationService demoApplicationService, ResponseMapper responseMapper) {
        this.demoApplicationService = demoApplicationService;
        this.responseMapper = responseMapper;
    }

    @GetMapping("/business-error")
    public ResponseEntity<BaseResponse<Map<String, Object>>> raiseBusinessError() {
        // Application Service에서 비즈니스 예외를 던지도록 유도
        demoApplicationService.raiseBusinessException();
        // 실제로는 위 코드에서 예외가 발생하므로 이 줄은 실행되지 않는다.
        return responseMapper.ok(Map.of("result", "ok"));
    }

    @GetMapping("/runtime-error")
    public ResponseEntity<BaseResponse<Map<String, Object>>> raiseRuntimeError() {
        // 처리되지 않은 런타임 예외 흐름을 확인하기 위한 코드
        throw new RuntimeException("unexpected runtime error");
    }

    @PostMapping("/validation")
    public ResponseEntity<BaseResponse<Map<String, String>>> validate(@Valid @RequestBody DemoValidationRequest request) {
        // @Valid 검증 성공 시에만 정상 응답
        return responseMapper.ok(Map.of("name", request.name()));
    }
}
