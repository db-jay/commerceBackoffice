package com.commerce.backoffice.presentation.common.exception;

import com.commerce.backoffice.domain.exception.BusinessException;
import com.commerce.backoffice.domain.exception.ErrorCode;
import com.commerce.backoffice.domain.exception.UnauthorizedException;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /*
     * 전역 예외 처리기.
     * Service/Controller에서 던진 예외를 한 곳에서 받아
     * 공통 응답 포맷(BaseResponse)으로 변환한다.
     */

    private final ResponseMapper responseMapper;

    public GlobalExceptionHandler(ResponseMapper responseMapper) {
        this.responseMapper = responseMapper;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBusinessException(BusinessException ex) {
        // 비즈니스 규칙 위반 예외
        return responseMapper.error(HttpStatus.CONFLICT, ex.code(), ex.errorMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<BaseResponse<Object>> handleValidationException(Exception ex) {
        // 요청 값 검증 실패 예외
        String message = validationMessage(ex);
        return responseMapper.error(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR.code(), message);
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse<Object>> handleUnauthorizedException(UnauthorizedException ex) {
        // 로그인 실패/토큰 검증 실패 등 인증 관련 예외
        return responseMapper.error(HttpStatus.UNAUTHORIZED, ex.code(), ex.errorMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Object>> handleRuntimeException(RuntimeException ex) {
        // 처리하지 못한 일반 런타임 예외
        return responseMapper.error(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR.code(),
            ErrorCode.INTERNAL_SERVER_ERROR.message()
        );
    }

    private String validationMessage(Exception ex) {
        // @Valid로 들어온 에러에서 사용자에게 보여줄 메시지를 추출
        if (ex instanceof MethodArgumentNotValidException validException) {
            FieldError fieldError = validException.getBindingResult().getFieldError();
            if (fieldError != null) {
                return fieldError.getDefaultMessage();
            }
        }
        // 파라미터 단위 검증(ConstraintViolation) 메시지 처리
        if (ex instanceof ConstraintViolationException constraintViolationException) {
            return constraintViolationException.getMessage();
        }
        // 어떤 이유로든 메시지 추출 실패 시 기본 메시지 반환
        return ErrorCode.VALIDATION_ERROR.message();
    }
}
