package org.laukvik.db.sql.cmd;

import org.laukvik.db.sql.DatabaseConnection;

/**
 * Created by morten on 17.10.2015.
 */
public abstract class SqlCommand implements Command {

    private final String action;
    private final String description;
    private final String parameter;
    DatabaseConnection db;

    public SqlCommand(String action, String parameter, String description) {
        this.action = action;
        this.parameter = parameter;
        this.description = description;
    }

    public SqlCommand(String action, String description) {
        this.action = action;
        this.parameter = null;
        this.description = description;
    }

    public void setDatabaseConnection(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getParameter() {
        return parameter;
    }
}
