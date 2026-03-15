package com.commerce.backoffice.presentation.auth.api;

import com.commerce.backoffice.application.auth.model.AuthTokenPair;
import com.commerce.backoffice.application.auth.port.in.AuthUseCase;
import com.commerce.backoffice.presentation.auth.api.dto.AuthTokenResponse;
import com.commerce.backoffice.presentation.auth.api.dto.LoginRequest;
import com.commerce.backoffice.presentation.auth.api.dto.RefreshTokenRequest;
import com.commerce.backoffice.presentation.auth.api.mapper.AuthPresentationMapper;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * [역할]
 * - 로그인/재발급 요청을 받는 인증 API 진입점이다.
 * - HTTP 요청을 Application UseCase가 이해할 수 있는 형태로 바꿔 전달한다.
 *
 * [왜 필요한가]
 * - Controller는 "웹 요청/응답 처리"에만 집중해야 한다.
 * - 실제 인증 판단은 Application/AuthUseCase가 담당해야 계층 역할이 깔끔해진다.
 *
 * [흐름]
 * - /api/auth/login   : 아이디/비밀번호 검증 후 Access/Refresh 발급
 * - /api/auth/refresh : Refresh 토큰 검증 후 새 토큰 쌍 발급
 *
 * [주의할 점]
 * - 이 클래스는 JWT를 직접 파싱하지 않는다.
 * - Request DTO -> Command 변환은 AuthPresentationMapper가 담당한다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final ResponseMapper responseMapper;
    private final AuthPresentationMapper presentationMapper;

    public AuthController(
        AuthUseCase authUseCase,
        ResponseMapper responseMapper,
        AuthPresentationMapper presentationMapper
    ) {
        this.authUseCase = authUseCase;
        this.responseMapper = responseMapper;
        this.presentationMapper = presentationMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthTokenResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) {
        // 1) HTTP 요청 DTO를 Application 입력(Command)으로 변환
        // 2) UseCase 실행
        // 3) 결과 토큰 쌍을 응답 DTO로 변환 후 공통 응답 포맷으로 반환
        AuthTokenPair tokenPair = authUseCase.login(presentationMapper.toLoginCommand(request));
        return responseMapper.ok(presentationMapper.toResponse(tokenPair));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthTokenResponse>> refresh(
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        // Refresh 토큰 재발급도 login과 동일한 패턴으로 흐른다.
        AuthTokenPair tokenPair = authUseCase.refresh(presentationMapper.toRefreshCommand(request));
        return responseMapper.ok(presentationMapper.toResponse(tokenPair));
    }
}
