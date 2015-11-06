package org.laukvik.db.sql.cmd;

import java.io.IOException;
import org.laukvik.db.sql.DatabaseExportFailedException;
import org.laukvik.db.sql.Exporter;

/**
 *
 *
 */
public class ExportScripts extends SqlCommand {

    public ExportScripts() {
        super("exportscripts", "Exports the database to SQL scripts");
    }

    @Override
    public int run(String value) {
        Exporter exporter = new Exporter(db);
        try {
            exporter.exportDatabaseScripts(db.getSchema());
            return SUCCESS;
        }
        catch (DatabaseExportFailedException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return EXCEPTION;
    }
}
