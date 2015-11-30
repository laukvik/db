package org.laukvik.db.cmd;

import java.util.Map;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.sql.Analyzer;

/**
 *
 *
 */
public class ListTables extends SqlCommand {

    public ListTables() {
        super("tables", "displays all tables");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        String catalog = null;
        String schema = null;
        Analyzer a = new Analyzer();
        for (Table t : a.findTables(catalog, schema, db)) {
            System.out.println(t.getName());
        }
        return 0;
    }
}
