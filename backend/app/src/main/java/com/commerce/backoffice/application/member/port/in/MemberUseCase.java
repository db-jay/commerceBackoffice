package com.commerce.backoffice.application.member.port.in;

import com.commerce.backoffice.application.member.command.ChangeMemberStatusCommand;
import com.commerce.backoffice.application.member.command.CreateMemberCommand;
import com.commerce.backoffice.application.member.command.UpdateMemberCommand;
import com.commerce.backoffice.domain.member.Member;

/*
 * Member 컨텍스트 인바운드 포트(UseCase).
 *
 * Controller는 이 인터페이스만 호출한다.
 * => 구현체를 바꿔도 Controller 코드는 거의 안 바뀐다.
 */
public interface MemberUseCase {

    Member create(CreateMemberCommand command);

    Member getById(long memberId);

    Member update(long memberId, UpdateMemberCommand command);

    Member changeStatus(long memberId, ChangeMemberStatusCommand command);
}
