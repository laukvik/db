package org.laukvik.sql.cmd;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.sql.DatabaseConnection;
import org.laukvik.sql.DatabaseConnectionNotFoundException;
import org.laukvik.sql.Exporter;

/**
 * Lists all connections
 *
 */
public class Backup extends SqlCommand {

    public Backup() {
        super("backup", "dir", "creates a backup");
    }

    @Override
    public int run(DatabaseConnection db, String directory) {
        Exporter exporter = new Exporter(db);
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (dir.isDirectory()) {
                System.out.println("Backup to: " + directory);
                exporter.backupCSV(dir);
                return SUCCESS;
            } else {
                System.out.println("Not a directory: " + directory);
                return EXCEPTION;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (DatabaseConnectionNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception ex) {
            Logger.getLogger(Backup.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ERROR;
    }
}
