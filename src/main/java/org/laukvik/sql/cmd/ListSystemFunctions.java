package org.laukvik.sql.cmd;

import org.laukvik.csv.columns.Function;
import org.laukvik.sql.Analyzer;
import org.laukvik.sql.DatabaseConnection;

/**
 *
 *
 */
public class ListSystemFunctions extends SqlCommand {

    public ListSystemFunctions() {
        super("system", "displays all system functions");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Analyzer a = new Analyzer();
        for (Function f : a.listSystemFunctions(db)) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
