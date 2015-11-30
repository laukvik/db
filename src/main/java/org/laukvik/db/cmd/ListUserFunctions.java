package org.laukvik.db.cmd;

import java.util.Map;
import org.laukvik.db.ddl.Function;
import org.laukvik.db.sql.Analyzer;

/**
 * Lists all user functions
 *
 */
public class ListUserFunctions extends SqlCommand {

    public ListUserFunctions() {
        super("functions", "lists all user functions");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        Analyzer a = new Analyzer();
        for (Function f : a.findUserFunctions(db.getSchema(), db)) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
