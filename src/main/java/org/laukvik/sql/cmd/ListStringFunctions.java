package org.laukvik.sql.cmd;

import org.laukvik.csv.columns.Function;
import org.laukvik.sql.Analyzer;
import org.laukvik.sql.DatabaseConnection;

/**
 *
 */
public class ListStringFunctions extends SqlCommand {

    public ListStringFunctions() {
        super("string", "displays all string functions");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Analyzer a = new Analyzer();
        for (Function f : a.listStringFunctions(db)) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
