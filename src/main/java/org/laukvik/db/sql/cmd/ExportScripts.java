package org.laukvik.db.sql.cmd;

import org.laukvik.db.sql.DatabaseConnection;
import org.laukvik.db.sql.DatabaseExportFailedException;
import org.laukvik.db.sql.Exporter;

import java.io.IOException;

/**
 *
 *
 */
public class ExportScripts extends SqlCommand {

    public ExportScripts() {
        super("exportscripts", "Exports the database to SQL scripts");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Exporter exporter = new Exporter(db);
        try {
            exporter.exportDatabaseScripts(db.getSchema());
            return SUCCESS;
        } catch (DatabaseExportFailedException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EXCEPTION;
    }
}
