package com.commerce.backoffice.domain.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.commerce.backoffice.support.fixture.TestFixtureFactory;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void withdraw_shouldChangeStatusToWithdrawn() {
        Member member = TestFixtureFactory.member(1L, "test@test.com", "tester");

        member.withdraw();

        assertEquals(MemberStatus.WITHDRAWN, member.status());
    }
}
