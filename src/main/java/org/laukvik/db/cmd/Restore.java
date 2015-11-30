package org.laukvik.db.cmd;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import org.laukvik.db.sql.DatabaseReadOnlyException;
import org.laukvik.db.sql.Importer;

public class Restore extends SqlCommand {

    public Restore() {
        super("restore", "directory", "Restores database from the specified directory");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        File directory = new File(value);
        if (directory.exists()) {
            try {
                Importer imp = new Importer(db);
                String encoding = props.get("encoding");
                Charset charset;
                if (encoding == null || encoding.trim().isEmpty()) {
                    charset = Charset.defaultCharset();
                } else {
                    try {
                        charset = Charset.forName(encoding);
                    }
                    catch (Exception e) {
                        System.out.println("Invalid encoding: " + encoding);
                        return EXCEPTION;
                    }
                }
                imp.importDirectory(directory, charset);
                return SUCCESS;
            }
            catch (DatabaseReadOnlyException e) {
                System.out.println("Connection is read only!");
            }
        } else {
            System.out.println("Directory not found!");
        }
        return EXCEPTION;
    }

}
