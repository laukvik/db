package org.laukvik.db.sql.cmd;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.db.sql.swing.Viewer;

/**
 * Starts the app
 *
 */
public class App extends SqlCommand {

    private static final Logger LOG = Logger.getLogger(App.class.getName());

    public App() {
        super("app", "Starts an application");
    }

    @Override
    public int run(String value) {
        LOG.log(Level.FINE, "Opening Swing application for database ''{0}''", (db == null ? "" : db.getFilename()));

        Viewer v = new Viewer();
        v.setDatabaseConnection(db);
        v.setVisible(true);

        return SUCCESS;
    }
}
