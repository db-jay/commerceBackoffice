package com.commerce.backoffice.presentation.health;

import java.util.Map;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    /*
     * 기본 헬스체크 API.
     * - 서비스 살아있는지 확인용 엔드포인트
     * - 응답도 공통 포맷(ResponseMapper)으로 내려준다.
     */

    private final ResponseMapper responseMapper;

    public HealthController(ResponseMapper responseMapper) {
        this.responseMapper = responseMapper;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Map<String, String>>> health() {
        // 비즈니스 데이터 대신 상태 값(UP)만 반환
        return responseMapper.ok(Map.of("status", "UP"));
    }
}
