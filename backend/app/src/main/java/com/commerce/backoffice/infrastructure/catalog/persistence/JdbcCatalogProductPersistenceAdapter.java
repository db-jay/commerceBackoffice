package com.commerce.backoffice.infrastructure.catalog.persistence;

import com.commerce.backoffice.application.catalog.port.out.CatalogProductPersistencePort;
import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.domain.catalog.ProductStatus;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/*
 * Catalog Output Port의 JDBC 구현체(Adapter).
 *
 * 초보자 포인트:
 * - 이 클래스는 SQL/DB 세부 구현만 담당한다.
 * - application은 이 구현체를 직접 참조하지 않고 Port 인터페이스만 참조한다.
 */
@Component
@ConditionalOnBean(JdbcTemplate.class)
@Primary
public class JdbcCatalogProductPersistenceAdapter implements CatalogProductPersistencePort {

    private static final RowMapper<Product> PRODUCT_ROW_MAPPER = (rs, rowNum) -> new Product(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getBigDecimal("price"),
        rs.getInt("stock_quantity"),
        ProductStatus.valueOf(rs.getString("status")),
        rs.getLong("version")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcCatalogProductPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product save(String name, BigDecimal price, int stockQuantity) {
        String sql = """
            insert into products (name, price, stock_quantity, status, version)
            values (?, ?, ?, ?, ?)
            returning id
            """;

        Long id = jdbcTemplate.queryForObject(
            sql,
            Long.class,
            name,
            price,
            stockQuantity,
            ProductStatus.ACTIVE.name(),
            0L
        );

        // returning id가 null이면 DB 응답이 비정상인 상태다.
        if (id == null) {
            throw new IllegalStateException("상품 저장 후 ID를 반환받지 못했습니다.");
        }

        return new Product(id, name, price, stockQuantity, ProductStatus.ACTIVE, 0L);
    }

    @Override
    public Optional<Product> findById(long productId) {
        String sql = """
            select id, name, price, stock_quantity, status, version
            from products
            where id = ?
            """;

        return jdbcTemplate.query(sql, PRODUCT_ROW_MAPPER, productId)
            .stream()
            .findFirst();
    }

    @Override
    public void updateBasicInfo(long productId, String name, BigDecimal price) {
        String sql = """
            update products
            set name = ?,
                price = ?,
                updated_at = current_timestamp
            where id = ?
            """;

        jdbcTemplate.update(sql, name, price, productId);
    }

    @Override
    public void updateStatus(long productId, ProductStatus status) {
        String sql = """
            update products
            set status = ?,
                updated_at = current_timestamp
            where id = ?
            """;

        jdbcTemplate.update(sql, status.name(), productId);
    }

    @Override
    public boolean updateStock(Product product) {
        String sql = """
            update products
            set stock_quantity = ?,
                version = version + 1,
                updated_at = current_timestamp
            where id = ?
              and version = ?
            """;

        return jdbcTemplate.update(sql, product.stockQuantity(), product.id(), product.version()) == 1;
    }
}
