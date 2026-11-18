package com.bcopstein.voos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;

class RetireLocalNetworkCatalogMigrationTest {
    @Test
    void removesLocalNetworkRowsAndForeignKeysWhilePreservingPlans() throws Exception {
        String url = "jdbc:h2:mem:retire-network-" + System.nanoTime() + ";DB_CLOSE_DELAY=-1";
        Flyway.configure()
                .dataSource(url, "sa", "")
                .target(MigrationVersion.fromVersion("7"))
                .load()
                .migrate();

        try (Connection connection = DriverManager.getConnection(url, "sa", "")) {
            assertEquals(0, count(connection, "SELECT COUNT(*) FROM AEROPORTOS"));
            assertEquals(0, count(connection, "SELECT COUNT(*) FROM AEROVIAS"));
            assertTrue(count(connection, "SELECT COUNT(*) FROM PLANOS_DE_VOO") > 0);

            for (String table : List.of("AEROVIAS", "PLANOS_DE_VOO", "TRECHOS", "RESERVAS")) {
                try (ResultSet keys = connection.getMetaData().getImportedKeys(null, null, table)) {
                    while (keys.next()) {
                        assertFalse("AEROPORTOS".equalsIgnoreCase(keys.getString("PKTABLE_NAME")));
                        assertFalse("AEROVIAS".equalsIgnoreCase(keys.getString("PKTABLE_NAME")));
                    }
                }
            }
        }
    }

    private long count(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getLong(1);
        }
    }
}
