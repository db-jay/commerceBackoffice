package com.commerce.backoffice.support.fixture;

import com.commerce.backoffice.application.auth.model.AuthTokenPair;
import com.commerce.backoffice.domain.auth.AuthenticatedOperator;
import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.domain.catalog.ProductStatus;
import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.domain.member.MemberGrade;
import com.commerce.backoffice.domain.member.MemberStatus;
import com.commerce.backoffice.domain.order.OrderLine;
import java.math.BigDecimal;

/*
 * [역할]
 * - 테스트에서 자주 만드는 객체를 짧고 읽기 쉽게 생성해주는 Fixture 모음이다.
 *
 * [왜 필요한가]
 * - 테스트마다 new Product(...), new Member(...)를 길게 적으면
 *   검증하려는 핵심보다 준비 코드가 더 눈에 띄게 된다.
 * - Fixture를 쓰면 "무슨 데이터를 쓰는지"가 더 빨리 보인다.
 *
 * [원칙]
 * - 지금 단계에서는 Builder보다 읽기 쉬운 정적 메서드 방식으로 유지한다.
 * - Fixture가 너무 복잡해지면 그때 Builder로 확장한다.
 */
public final class TestFixtureFactory {

    private TestFixtureFactory() {
    }

    public static Product product(long id, String name, int price, int stockQuantity) {
        return new Product(id, name, BigDecimal.valueOf(price), stockQuantity, ProductStatus.ACTIVE);
    }

    public static Member member(long id, String email, String name) {
        return new Member(id, email, name, MemberGrade.BASIC, MemberStatus.ACTIVE);
    }

    public static OrderLine orderLine(long productId, int quantity, int unitPrice) {
        return new OrderLine(productId, quantity, BigDecimal.valueOf(unitPrice));
    }

    public static AuthenticatedOperator operator(String subject, String role) {
        return new AuthenticatedOperator(subject, role);
    }

    public static AuthTokenPair tokenPair(String accessToken, String refreshToken) {
        return new AuthTokenPair(accessToken, refreshToken, "Bearer", 1800L, 1209600L);
    }
}
