package com.commerce.backoffice.infrastructure.display.persistence;

import com.commerce.backoffice.application.display.port.out.DisplayEventPersistencePort;
import com.commerce.backoffice.domain.display.DisplayEvent;
import com.commerce.backoffice.domain.display.DisplayEventStatus;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(JdbcTemplate.class)
@Primary
public class JdbcDisplayEventPersistenceAdapter implements DisplayEventPersistencePort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDisplayEventPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DisplayEvent save(
        String name,
        DisplayEventStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Set<Long> productIds
    ) {
        String eventSql = """
            insert into display_events (name, status, start_at, end_at)
            values (?, ?, ?, ?)
            returning id
            """;

        Long eventId = jdbcTemplate.queryForObject(
            eventSql,
            Long.class,
            name,
            status.name(),
            Timestamp.valueOf(startAt),
            Timestamp.valueOf(endAt)
        );

        if (eventId == null) {
            throw new IllegalStateException("전시 이벤트 저장 후 ID를 반환받지 못했습니다.");
        }

        String targetSql = """
            insert into display_event_products (event_id, product_id)
            values (?, ?)
            """;
        for (Long productId : productIds) {
            jdbcTemplate.update(targetSql, eventId, productId);
        }

        return DisplayEvent.restore(eventId, name, status, startAt, endAt, productIds);
    }

    @Override
    public List<DisplayEvent> findByProductId(long productId) {
        String sql = """
            select e.id, e.name, e.status, e.start_at, e.end_at
            from display_events e
            join display_event_products ep on ep.event_id = e.id
            where ep.product_id = ?
            order by e.start_at asc, e.id asc
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> DisplayEvent.restore(
                rs.getLong("id"),
                rs.getString("name"),
                DisplayEventStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("start_at").toLocalDateTime(),
                rs.getTimestamp("end_at").toLocalDateTime(),
                singletonProductId(productId)
            ),
            productId
        );
    }

    private Set<Long> singletonProductId(long productId) {
        List<Long> ids = new ArrayList<>();
        ids.add(productId);
        return new LinkedHashSet<>(ids);
    }
}
