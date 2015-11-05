package org.laukvik.sql.cmd;

import org.laukvik.csv.columns.Function;
import org.laukvik.sql.Analyzer;
import org.laukvik.sql.DatabaseConnection;

/**
 * Lists date functions
 *
 */
public class ListDateFunctions extends SqlCommand {

    public ListDateFunctions() {
        super("date", "displays all date functions");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Analyzer a = new Analyzer();
        for (Function f : a.listTimeDateFunctions(db)) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
