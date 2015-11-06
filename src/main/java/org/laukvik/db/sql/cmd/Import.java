package org.laukvik.db.sql.cmd;

import org.laukvik.db.sql.DatabaseConnection;
import org.laukvik.db.sql.DatabaseReadOnlyException;
import org.laukvik.db.sql.Importer;
import org.laukvik.db.sql.SQL;

/**
 *
 *
 */
public class Import extends SqlCommand {

    public Import() {
        super("import", "Imports the database from file");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        try {
            Importer importer = new Importer(db);
            return SUCCESS;
        } catch (DatabaseReadOnlyException e) {
            e.printStackTrace();
        }

        return EXCEPTION;
    }
}
