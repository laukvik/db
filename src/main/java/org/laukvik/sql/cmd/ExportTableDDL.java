package org.laukvik.sql.cmd;

import java.io.IOException;
import org.laukvik.csv.columns.Schema;
import org.laukvik.csv.columns.Table;
import org.laukvik.sql.Analyzer;
import org.laukvik.sql.DatabaseConnection;

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
