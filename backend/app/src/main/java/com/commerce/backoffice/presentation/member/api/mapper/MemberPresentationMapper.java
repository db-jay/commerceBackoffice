package com.commerce.backoffice.presentation.member.api.mapper;

import com.commerce.backoffice.application.member.command.ChangeMemberStatusCommand;
import com.commerce.backoffice.application.member.command.CreateMemberCommand;
import com.commerce.backoffice.application.member.command.UpdateMemberCommand;
import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.presentation.member.api.dto.ChangeMemberStatusRequest;
import com.commerce.backoffice.presentation.member.api.dto.CreateMemberRequest;
import com.commerce.backoffice.presentation.member.api.dto.MemberResponse;
import com.commerce.backoffice.presentation.member.api.dto.UpdateMemberRequest;

/*
 * Member 프레젠테이션 매퍼 계약.
 *
 * 왜 분리하나?
 * - Controller는 HTTP 처리에 집중하고,
 * - DTO <-> Command/Response 변환 규칙은 별도 객체에서 관리하기 위해서다.
 */
public interface MemberPresentationMapper {

    CreateMemberCommand toCreateCommand(CreateMemberRequest request);

    UpdateMemberCommand toUpdateCommand(UpdateMemberRequest request);

    ChangeMemberStatusCommand toChangeStatusCommand(ChangeMemberStatusRequest request);

    MemberResponse toResponse(Member member);
}
