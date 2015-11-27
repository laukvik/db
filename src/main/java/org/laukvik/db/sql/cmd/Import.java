package org.laukvik.db.sql.cmd;

import java.util.Map;
import org.laukvik.db.sql.DatabaseReadOnlyException;
import org.laukvik.db.sql.Importer;

/**
 *
 *
 */
public class Import extends SqlCommand {

    public Import() {
        super("import", "Imports the database from file");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        try {
            Importer importer = new Importer(db);
            return SUCCESS;
        }
        catch (DatabaseReadOnlyException e) {
            e.printStackTrace();
        }

        return EXCEPTION;
    }
}
