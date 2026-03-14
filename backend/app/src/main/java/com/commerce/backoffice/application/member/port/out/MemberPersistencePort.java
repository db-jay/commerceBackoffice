package com.commerce.backoffice.application.member.port.out;

import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.domain.member.MemberGrade;
import com.commerce.backoffice.domain.member.MemberStatus;
import java.util.Optional;

/*
 * Member 영속화 아웃바운드 포트.
 * - application은 DB 구현(JDBC/JPA)을 모르고 이 계약만 안다.
 */
public interface MemberPersistencePort {

    Member save(String email, String name);

    Optional<Member> findById(long memberId);

    void updateProfile(long memberId, String name, MemberGrade grade);

    void updateStatus(long memberId, MemberStatus status);
}
