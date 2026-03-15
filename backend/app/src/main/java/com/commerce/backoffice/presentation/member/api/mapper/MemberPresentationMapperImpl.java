package com.commerce.backoffice.presentation.member.api.mapper;

import com.commerce.backoffice.application.member.command.ChangeMemberStatusCommand;
import com.commerce.backoffice.application.member.command.CreateMemberCommand;
import com.commerce.backoffice.application.member.command.UpdateMemberCommand;
import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.presentation.member.api.dto.ChangeMemberStatusRequest;
import com.commerce.backoffice.presentation.member.api.dto.CreateMemberRequest;
import com.commerce.backoffice.presentation.member.api.dto.MemberResponse;
import com.commerce.backoffice.presentation.member.api.dto.UpdateMemberRequest;
import org.springframework.stereotype.Component;

/*
 * Member 프레젠테이션 매퍼 구현체.
 *
 * 현재 선택:
 * - 수동 매핑(manual mapping)
 * 이유:
 * - 입문자가 디버깅할 때 값 흐름을 한 줄씩 추적하기 쉽다.
 * - 필드가 많아져 반복이 커지면 이후 MapStruct 도입 시점으로 전환 가능하다.
 */
@Component
public class MemberPresentationMapperImpl implements MemberPresentationMapper {

    @Override
    public CreateMemberCommand toCreateCommand(CreateMemberRequest request) {
        return new CreateMemberCommand(request.email(), request.name());
    }

    @Override
    public UpdateMemberCommand toUpdateCommand(UpdateMemberRequest request) {
        return new UpdateMemberCommand(request.name(), request.grade());
    }

    @Override
    public ChangeMemberStatusCommand toChangeStatusCommand(ChangeMemberStatusRequest request) {
        return new ChangeMemberStatusCommand(request.status());
    }

    @Override
    public MemberResponse toResponse(Member member) {
        return new MemberResponse(
            member.id(),
            member.email(),
            member.name(),
            member.grade().name(),
            member.status().name()
        );
    }
}
