package org.laukvik.db.cmd;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.db.sql.DatabaseConnection;
import org.laukvik.db.sql.swing.Viewer;

/**
 * Starts the app
 *
 */
public class App implements Command {

    private static final Logger LOG = Logger.getLogger(App.class.getName());

    public App() {
    }

    @Override
    public String getAction() {
        return "app";
    }

    @Override
    public String getDescription() {
        return "Starts an application";
    }

    @Override
    public int run(String value, Map<String, String> props) {
        LOG.log(Level.INFO, "Opening Swing application for database ");

        Viewer v = new Viewer();

        DatabaseConnection db = new DatabaseConnection();

        v.setDatabaseConnection(db);
        v.setVisible(true);

        return SUCCESS;
    }

    @Override
    public String getParameter() {
        return null;
    }
}
