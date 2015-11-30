package org.laukvik.db.cmd;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Starts the app
 *
 */
public class Wizard extends SqlCommand {

    private static final Logger LOG = Logger.getLogger(Wizard.class.getName());

    public Wizard() {
        super("wizard", "shows the row count in each table");
    }

    @Override
    public int run(String value, Map<String, String> props) {

        String driver = db.getDriver();
        String server = db.getServer();
        String database = db.getDatabase();
        String user = db.getUser();
        String password = db.getPassword();
        String port = db.getPort();
        String url = db.getUrl();

        driver = System.console().readLine("Driver (%s): ", db.getDriver());
        server = System.console().readLine("Server (%s): ", db.getServer());
        database = System.console().readLine("Database (%s): ", db.getDatabase());
        user = System.console().readLine("User (%s): ", db.getUser());
        password = System.console().readLine("Password (%s): ", db.getPassword());
        port = System.console().readLine("Port (%s): ", db.getPort());
        url = System.console().readLine("URL (%s): ", db.getUrl());

        return SUCCESS;
    }
}
