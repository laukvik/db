package org.laukvik.db.sql.cmd;

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
    public int run(String value) {
        Exporter exporter = new Exporter(db);
        exporter.listQuery(value);
        return SUCCESS;
    }
}
