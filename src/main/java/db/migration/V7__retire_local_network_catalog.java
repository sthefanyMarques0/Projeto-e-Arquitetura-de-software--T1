package db.migration;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V7__retire_local_network_catalog extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        List<String> constraints = foreignKeysToLocalCatalog(context.getConnection().getMetaData());
        try (Statement statement = context.getConnection().createStatement()) {
            for (String constraint : constraints) {
                statement.execute("ALTER TABLE " + constraintTable(constraint) + " DROP CONSTRAINT " + constraintName(constraint));
            }
            statement.executeUpdate("DELETE FROM aerovias");
            statement.executeUpdate("DELETE FROM aeroportos");
        }
    }

    private static List<String> foreignKeysToLocalCatalog(DatabaseMetaData metadata) throws SQLException {
        List<String> constraints = new ArrayList<>();
        for (String table : List.of("AEROVIAS", "PLANOS_DE_VOO", "TRECHOS", "RESERVAS")) {
            try (ResultSet keys = metadata.getImportedKeys(null, null, table)) {
                while (keys.next()) {
                    String referenced = keys.getString("PKTABLE_NAME");
                    if ("AEROPORTOS".equalsIgnoreCase(referenced) || "AEROVIAS".equalsIgnoreCase(referenced)) {
                        constraints.add(table + ":" + keys.getString("FK_NAME"));
                    }
                }
            }
        }
        return constraints;
    }

    private static String constraintTable(String key) {
        return key.substring(0, key.indexOf(':'));
    }

    private static String constraintName(String key) {
        return key.substring(key.indexOf(':') + 1);
    }
}
