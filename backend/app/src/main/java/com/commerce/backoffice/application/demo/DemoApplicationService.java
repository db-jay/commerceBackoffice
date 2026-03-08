package com.commerce.backoffice.application.demo;

import com.commerce.backoffice.domain.exception.BusinessException;
import com.commerce.backoffice.domain.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class DemoApplicationService {
    /*
     * 실제 도메인 로직 대신 예외 흐름을 학습하기 위한 데모 서비스.
     * Controller -> Application Service -> Exception -> ControllerAdvice 흐름을 보여준다.
     */

    public void raiseBusinessException() {
        // "비즈니스 규칙 위반" 상황을 강제로 발생시킨다.
        throw new BusinessException(ErrorCode.DEMO_BUSINESS_ERROR);
    }
}
