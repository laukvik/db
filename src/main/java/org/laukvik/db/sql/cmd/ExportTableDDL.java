package org.laukvik.db.sql.cmd;

import java.io.IOException;
import java.util.Map;
import org.laukvik.db.ddl.Schema;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.sql.Analyzer;

/**
 *
 *
 */
public class ExportTableDDL extends SqlCommand {

    public ExportTableDDL() {
        super("tabledef", "Exports the table definition");
    }

    @Override
    public int run(String value, Map<String, String> props) {

        Analyzer a = new Analyzer();
        try {
            Schema s = a.findSchema(db.getSchema(), db);
            for (Table t : s.getTables()) {
                System.out.println(t.getDDL());
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            return EXCEPTION;
        }

        return SUCCESS;
    }
}
