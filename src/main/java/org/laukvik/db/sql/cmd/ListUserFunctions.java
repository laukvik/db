package org.laukvik.db.sql.cmd;

import org.laukvik.db.ddl.Function;
import org.laukvik.db.sql.Analyzer;
import org.laukvik.db.sql.DatabaseConnection;

/**
 * Lists all user functions
 *
 */
public class ListUserFunctions extends SqlCommand {

    public ListUserFunctions() {
        super("functions", "lists all user functions");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Analyzer a = new Analyzer();
        for (Function f : a.findUserFunctions(db.getSchema(), db)) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
