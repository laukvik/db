package org.laukvik.db.cmd;

import java.util.Map;
import org.laukvik.db.ddl.Function;
import org.laukvik.db.sql.Analyzer;

/**
 *
 *
 */
public class ListSystemFunctions extends SqlCommand {

    public ListSystemFunctions() {
        super("system", "displays all system functions");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        Analyzer a = new Analyzer();
        for (Function f : a.listSystemFunctions(db)) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
