package org.laukvik.db.sql.cmd;

import org.laukvik.db.ddl.Table;
import org.laukvik.db.sql.Analyzer;
import org.laukvik.db.sql.DatabaseConnection;

/**
 *
 *
 */
public class ListTables extends SqlCommand {

    public ListTables() {
        super("tables", "displays all tables");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Analyzer a = new Analyzer();
        for (Table t : a.findTables(db.getSchema(), db)) {
            System.out.println(t.getName());
        }
        return 0;
    }
}
