package org.laukvik.sql.cmd;

import org.laukvik.csv.columns.Table;
import org.laukvik.sql.Analyzer;
import org.laukvik.sql.DatabaseConnection;

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
