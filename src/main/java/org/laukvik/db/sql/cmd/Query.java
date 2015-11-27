package org.laukvik.db.sql.cmd;

import java.util.Map;
import org.laukvik.db.sql.Exporter;

/**
 * Lists all connections
 *
 */
public class Query extends SqlCommand {

    public Query() {
        super("query", "SQL", "displays all registered connections");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        Exporter exporter = new Exporter(db);
        exporter.listQuery(value);
        return SUCCESS;
    }
}
