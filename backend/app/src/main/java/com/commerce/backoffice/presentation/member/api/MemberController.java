package com.commerce.backoffice.presentation.member.api;

import com.commerce.backoffice.application.member.port.in.MemberUseCase;
import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.presentation.member.api.dto.ChangeMemberStatusRequest;
import com.commerce.backoffice.presentation.member.api.dto.CreateMemberRequest;
import com.commerce.backoffice.presentation.member.api.dto.MemberResponse;
import com.commerce.backoffice.presentation.member.api.dto.UpdateMemberRequest;
import com.commerce.backoffice.presentation.member.api.mapper.MemberPresentationMapper;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
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
 * Member API Controller.
 *
 * 역할:
 * - HTTP 입력 검증
 * - UseCase 호출
 * - 응답 DTO 변환
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
