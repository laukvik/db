package org.laukvik.db.sql.cmd;

import java.io.File;
import java.nio.charset.Charset;
import org.laukvik.db.sql.DatabaseReadOnlyException;
import org.laukvik.db.sql.Importer;

/**
 *
 *
 */
public class Restore extends SqlCommand {

    public Restore() {
        super("restore", "directory", "Restores database from the specified directory");
    }

    @Override
    public int run(String value) {
        File directory = new File(value);
        if (directory.exists()) {
            try {
                Importer imp = new Importer(db);
                /* @todo - Add support for encoding */
                imp.importDirectory(directory, Charset.forName("iso-8859-1"));
                return SUCCESS;
            }
            catch (DatabaseReadOnlyException e) {
                System.out.println("Connection is read only!");
            }
        }
        return EXCEPTION;
    }

}
