package org.laukvik.db.sql.cmd;

import java.io.IOException;
import org.laukvik.db.ddl.Schema;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.sql.Analyzer;
import org.laukvik.db.sql.DatabaseConnection;

/**
 *
 *
 */
public class ExportTableDDL extends SqlCommand {

    public ExportTableDDL() {
        super("tabledef", "Exports the table definition");
    }

    @Override
    public int run(DatabaseConnection db, String value) {

        Analyzer a = new Analyzer();
        try {
            Schema s = a.findSchema(null, db);
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
