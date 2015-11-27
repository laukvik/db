package org.laukvik.db.sql.cmd;

import java.util.Map;
import org.laukvik.db.ddl.Function;
import org.laukvik.db.sql.Analyzer;

public class ListNumericFunctions extends SqlCommand {

    public ListNumericFunctions() {
        super("numeric", "displays all numeric functions");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        Analyzer a = new Analyzer();
        for (Function f : a.listNumbericFunctions(db)) {
            System.out.println(f.getName());
        }
        return 0;
    }

}
