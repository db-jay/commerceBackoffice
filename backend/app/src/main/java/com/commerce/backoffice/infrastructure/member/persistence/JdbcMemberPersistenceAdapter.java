package com.commerce.backoffice.infrastructure.member.persistence;

import com.commerce.backoffice.application.member.port.out.MemberPersistencePort;
import com.commerce.backoffice.domain.member.Member;
import com.commerce.backoffice.domain.member.MemberGrade;
import com.commerce.backoffice.domain.member.MemberStatus;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/*
 * Member 아웃바운드 포트의 JDBC 구현체.
 * - SQL/DB 세부 구현을 이 어댑터에만 모은다.
 */
@Component
@Primary
@ConditionalOnBean(JdbcTemplate.class)
public class JdbcMemberPersistenceAdapter implements MemberPersistencePort {

    private static final RowMapper<Member> MEMBER_ROW_MAPPER = (rs, rowNum) -> new Member(
        rs.getLong("id"),
        rs.getString("email"),
        rs.getString("name"),
        MemberGrade.valueOf(rs.getString("grade")),
        MemberStatus.valueOf(rs.getString("status"))
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Member save(String email, String name) {
        String sql = """
            insert into members (email, name, grade, status)
            values (?, ?, ?, ?)
            returning id
            """;

        Long id = jdbcTemplate.queryForObject(
            sql,
            Long.class,
            email,
            name,
            MemberGrade.BASIC.name(),
            MemberStatus.ACTIVE.name()
        );

        if (id == null) {
            throw new IllegalStateException("회원 저장 후 ID를 반환받지 못했습니다.");
        }

        return new Member(id, email, name, MemberGrade.BASIC, MemberStatus.ACTIVE);
    }

    @Override
    public Optional<Member> findById(long memberId) {
        String sql = """
            select id, email, name, grade, status
            from members
            where id = ?
            """;

        return jdbcTemplate.query(sql, MEMBER_ROW_MAPPER, memberId)
            .stream()
            .findFirst();
    }

    @Override
    public void updateProfile(long memberId, String name, MemberGrade grade) {
        String sql = """
            update members
            set name = ?,
                grade = ?,
                updated_at = current_timestamp
            where id = ?
            """;

        jdbcTemplate.update(sql, name, grade.name(), memberId);
    }

    @Override
    public void updateStatus(long memberId, MemberStatus status) {
        String sql = """
            update members
            set status = ?,
                updated_at = current_timestamp
            where id = ?
            """;

        jdbcTemplate.update(sql, status.name(), memberId);
    }
}
