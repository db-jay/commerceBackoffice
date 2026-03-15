package com.commerce.backoffice.presentation.member.api;

import com.commerce.backoffice.application.member.port.in.MemberUseCase;
import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
import com.commerce.backoffice.presentation.member.api.dto.ChangeMemberStatusRequest;
import com.commerce.backoffice.presentation.member.api.dto.CreateMemberRequest;
import com.commerce.backoffice.presentation.member.api.dto.MemberResponse;
import com.commerce.backoffice.presentation.member.api.dto.UpdateMemberRequest;
import com.commerce.backoffice.presentation.member.api.mapper.MemberPresentationMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * [역할]
 * - 회원 관련 HTTP 요청을 받는 Presentation 진입점이다.
 *
 * [왜 필요한가]
 * - Controller는 웹 요청/응답 처리에만 집중해야 한다.
 * - 실제 회원 생성/조회/수정 규칙은 Application UseCase가 담당한다.
 *
 * [흐름]
 * - 요청 DTO 수신
 * - @Valid 1차 검증
 * - Mapper로 Command/Response 변환
 * - UseCase 호출
 * - 공통 응답(BaseResponse) 반환
 *
 * [주의할 점]
 * - Controller가 Domain 규칙을 직접 구현하면 안 된다.
 * - DTO <-> Response/Command 변환은 MemberPresentationMapper가 담당한다.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberUseCase memberUseCase;
    private final ResponseMapper responseMapper;
    private final MemberPresentationMapper presentationMapper;

    public MemberController(
        MemberUseCase memberUseCase,
        ResponseMapper responseMapper,
        MemberPresentationMapper presentationMapper
    ) {
        this.memberUseCase = memberUseCase;
        this.responseMapper = responseMapper;
        this.presentationMapper = presentationMapper;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<MemberResponse>> create(
        @Valid @RequestBody CreateMemberRequest request
    ) {
        Member member = memberUseCase.create(presentationMapper.toCreateCommand(request));
        return responseMapper.ok(presentationMapper.toResponse(member));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<BaseResponse<MemberResponse>> getById(@PathVariable long memberId) {
        Member member = memberUseCase.getById(memberId);
        return responseMapper.ok(presentationMapper.toResponse(member));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<BaseResponse<MemberResponse>> update(
        @PathVariable long memberId,
        @Valid @RequestBody UpdateMemberRequest request
    ) {
        Member member = memberUseCase.update(
            memberId,
            presentationMapper.toUpdateCommand(request)
        );
        return responseMapper.ok(presentationMapper.toResponse(member));
    }

    @PatchMapping("/{memberId}/status")
    public ResponseEntity<BaseResponse<MemberResponse>> changeStatus(
        @PathVariable long memberId,
        @Valid @RequestBody ChangeMemberStatusRequest request
    ) {
        Member member = memberUseCase.changeStatus(
            memberId,
            presentationMapper.toChangeStatusCommand(request)
        );
        return responseMapper.ok(presentationMapper.toResponse(member));
    }
}
