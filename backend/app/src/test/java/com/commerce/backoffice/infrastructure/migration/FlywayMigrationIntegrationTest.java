package com.commerce.backoffice.infrastructure.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/*
 * Week1 보강 통합 테스트.
 *
 * 왜 이 테스트가 필요한가?
 * - "SQL 파일이 있다"와 "실제 DB에 적용된다"는 다른 이야기다.
 * - 이 테스트는 PostgreSQL 컨테이너를 실제로 띄운 뒤,
 *   Flyway 마이그레이션을 실행해서 테이블 생성 여부를 검증한다.
 *
 * 초보자 관점 핵심:
 * 1) 테스트 시작 시 임시 PostgreSQL이 만들어진다.
 * 2) Flyway가 db/migration 아래 SQL을 실행한다.
 * 3) members/products/orders/order_items 테이블 존재를 직접 확인한다.
 */
@Testcontainers(disabledWithoutDocker = true)
class FlywayMigrationIntegrationTest {

    /*
     * @Container가 붙은 객체는 테스트 시작/종료에 맞춰 자동으로 관리된다.
     * - 이 컨테이너는 테스트 전용이므로 운영 DB를 건드리지 않는다.
     */
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("commerce_test")
        .withUsername("test")
        .withPassword("test");

    @Test
    void migrate_shouldCreateWeek1DraftTables() throws Exception {
        // Flyway 설정: 테스트 컨테이너 DB에 마이그레이션을 적용한다.
        Flyway flyway = Flyway.configure()
            .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .load();

        // 실제 SQL 실행.
        flyway.migrate();

        // 적용 결과를 DB에서 직접 조회해 테이블 존재 여부를 검증한다.
        assertThat(tableExists("members")).isTrue();
        assertThat(tableExists("products")).isTrue();
        assertThat(tableExists("orders")).isTrue();
        assertThat(tableExists("order_items")).isTrue();
    }

    private boolean tableExists(String tableName) throws Exception {
        String sql = """
            select exists (
                select 1
                from information_schema.tables
                where table_schema = 'public'
                  and table_name = '%s'
            )
            """.formatted(tableName);

        try (Connection connection = java.sql.DriverManager.getConnection(
            postgres.getJdbcUrl(),
            postgres.getUsername(),
            postgres.getPassword()
        );
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getBoolean(1);
        }
    }
}
