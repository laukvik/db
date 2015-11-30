package org.laukvik.db.cmd;

import java.util.Map;
import org.laukvik.db.ddl.Function;
import org.laukvik.db.sql.Analyzer;

/**
 * Lists date functions
 *
 */
public class ListDateFunctions extends SqlCommand {

    public ListDateFunctions() {
        super("date", "displays all date functions");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        Analyzer a = new Analyzer();
        for (Function f : a.listTimeDateFunctions(db)) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
