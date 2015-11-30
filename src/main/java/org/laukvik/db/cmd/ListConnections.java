package org.laukvik.db.cmd;

import java.util.Map;
import org.laukvik.db.sql.Analyzer;
import org.laukvik.db.sql.DatabaseConnection;

/**
 * Lists all connections
 *
 */
public class ListConnections implements Command {

    public ListConnections() {
    }

    public int run(String value, Map<String, String> props) {
        for (DatabaseConnection c : Analyzer.findDatabaseConnections()) {
            System.out.println(c.getFilename());
        }
        return Command.SUCCESS;
    }

    @Override
    public String getAction() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "displays all registered connections";
    }

    @Override
    public String getParameter() {
        return null;
    }
}
