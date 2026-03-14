package com.commerce.backoffice.application.member.service;

import com.commerce.backoffice.application.member.command.ChangeMemberStatusCommand;
import com.commerce.backoffice.application.member.command.CreateMemberCommand;
import com.commerce.backoffice.application.member.command.UpdateMemberCommand;
import com.commerce.backoffice.application.member.port.in.MemberUseCase;
import com.commerce.backoffice.application.member.port.out.MemberPersistencePort;
import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.domain.member.MemberGrade;
import com.commerce.backoffice.domain.member.MemberStatus;
import com.commerce.backoffice.domain.exception.BusinessException;
import com.commerce.backoffice.domain.exception.ErrorCode;
import org.springframework.stereotype.Service;

/*
 * Member 유스케이스 구현체(Application Service).
 *
 * 역할:
 * 1) 입력 커맨드를 받아 도메인 흐름을 조합
 * 2) Port를 통해 영속화 수행
 * 3) 찾기 실패 같은 비즈니스 예외를 공통 ErrorCode로 변환
 */
@Service
public class MemberApplicationService implements MemberUseCase {

    private final MemberPersistencePort memberPersistencePort;

    public MemberApplicationService(MemberPersistencePort memberPersistencePort) {
        this.memberPersistencePort = memberPersistencePort;
    }

    @Override
    public Member create(CreateMemberCommand command) {
        return memberPersistencePort.save(command.email(), command.name());
    }

    @Override
    public Member getById(long memberId) {
        return memberPersistencePort.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    public Member update(long memberId, UpdateMemberCommand command) {
        Member member = memberPersistencePort.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        MemberGrade targetGrade = MemberGrade.valueOf(command.grade());
        member.changeName(command.name());
        member.upgradeGrade(targetGrade);
        memberPersistencePort.updateProfile(member.id(), member.name(), member.grade());
        return member;
    }

    @Override
    public Member changeStatus(long memberId, ChangeMemberStatusCommand command) {
        Member member = memberPersistencePort.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        MemberStatus targetStatus = MemberStatus.valueOf(command.status());
        switch (targetStatus) {
            case ACTIVE -> member.activate();
            case DORMANT -> member.deactivate();
            case WITHDRAWN -> member.withdraw();
        }

        memberPersistencePort.updateStatus(member.id(), member.status());
        return member;
    }
}
