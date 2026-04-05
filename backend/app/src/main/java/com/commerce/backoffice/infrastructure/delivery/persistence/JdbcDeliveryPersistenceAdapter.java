package com.commerce.backoffice.infrastructure.delivery.persistence;

import com.commerce.backoffice.application.delivery.port.out.DeliveryPersistencePort;
import com.commerce.backoffice.domain.delivery.Delivery;
import com.commerce.backoffice.domain.delivery.DeliveryStatus;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnBean(JdbcTemplate.class)
public class JdbcDeliveryPersistenceAdapter implements DeliveryPersistencePort {

    private static final RowMapper<Delivery> DELIVERY_ROW_MAPPER = (rs, rowNum) -> Delivery.restore(
        rs.getLong("id"),
        rs.getLong("order_id"),
        DeliveryStatus.valueOf(rs.getString("delivery_status")),
        rs.getString("tracking_number")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcDeliveryPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Delivery save(long orderId) {
        String sql = """
            insert into deliveries (order_id, delivery_status)
            values (?, ?)
            returning id
            """;

        Long deliveryId = jdbcTemplate.queryForObject(
            sql,
            Long.class,
            orderId,
            DeliveryStatus.READY.name()
        );

        if (deliveryId == null) {
            throw new IllegalStateException("배송 저장 후 ID를 반환받지 못했습니다.");
        }

        return Delivery.create(deliveryId, orderId);
    }

    @Override
    public Optional<Delivery> findById(long deliveryId) {
        String sql = """
            select id, order_id, delivery_status, tracking_number
            from deliveries
            where id = ?
            """;

        return jdbcTemplate.query(sql, DELIVERY_ROW_MAPPER, deliveryId)
            .stream()
            .findFirst();
    }

    @Override
    public void update(Delivery delivery) {
        String sql = """
            update deliveries
            set delivery_status = ?,
                tracking_number = ?,
                updated_at = current_timestamp
            where id = ?
            """;

        jdbcTemplate.update(
            sql,
            delivery.status().name(),
            delivery.trackingNumber(),
            delivery.id()
        );
    }
}
