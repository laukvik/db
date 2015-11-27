package org.laukvik.db.sql.cmd;

import java.util.Map;
import org.laukvik.db.ddl.Function;
import org.laukvik.db.sql.Analyzer;

/**
 *
 */
public class ListStringFunctions extends SqlCommand {

    public ListStringFunctions() {
        super("string", "displays all string functions");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        Analyzer a = new Analyzer();
        for (Function f : a.listStringFunctions(db)) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
