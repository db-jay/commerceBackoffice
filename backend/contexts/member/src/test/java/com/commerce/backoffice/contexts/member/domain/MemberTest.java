package com.commerce.backoffice.contexts.member.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void withdraw_shouldChangeStatusToWithdrawn() {
        Member member = new Member(1L, "test@test.com", "tester", MemberGrade.BASIC, MemberStatus.ACTIVE);

        member.withdraw();

        assertEquals(MemberStatus.WITHDRAWN, member.status());
    }
}

