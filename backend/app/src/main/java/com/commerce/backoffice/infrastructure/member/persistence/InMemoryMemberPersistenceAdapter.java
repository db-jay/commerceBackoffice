package com.commerce.backoffice.infrastructure.member.persistence;

import com.commerce.backoffice.application.member.port.out.MemberPersistencePort;
import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.domain.member.MemberGrade;
import com.commerce.backoffice.domain.member.MemberStatus;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/*
 * DB 없이도 컨텍스트 로딩/기본 흐름 테스트가 가능하도록 둔 인메모리 어댑터.
 */
@Component
public class InMemoryMemberPersistenceAdapter implements MemberPersistencePort {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, Member> members = new ConcurrentHashMap<>();

    @Override
    public Member save(String email, String name) {
        long id = sequence.getAndIncrement();
        Member member = new Member(id, email, name, MemberGrade.BASIC, MemberStatus.ACTIVE);
        members.put(id, member);
        return member;
    }

    @Override
    public Optional<Member> findById(long memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    @Override
    public void updateProfile(long memberId, String name, MemberGrade grade) {
        Member current = members.get(memberId);
        if (current == null) {
            return;
        }

        Member replaced = new Member(
            current.id(),
            current.email(),
            name,
            grade,
            current.status()
        );
        members.put(memberId, replaced);
    }

    @Override
    public void updateStatus(long memberId, MemberStatus status) {
        Member current = members.get(memberId);
        if (current == null) {
            return;
        }

        Member replaced = new Member(
            current.id(),
            current.email(),
            current.name(),
            current.grade(),
            status
        );
        members.put(memberId, replaced);
    }
}
