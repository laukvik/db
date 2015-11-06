package org.laukvik.db.sql.cmd;

import org.laukvik.db.sql.Analyzer;
import org.laukvik.db.sql.DatabaseConnection;
import org.laukvik.db.sql.SQL;

/**
 * Lists all connections
 *
 */
public class ListConnections extends SqlCommand {

    public ListConnections() {
        super("list", "displays all registered connections");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Analyzer a = new Analyzer();
        for (DatabaseConnection c : a.findDatabaseConnections()){
            System.out.println(c.getFilename());
        }
        return 0;
    }
}
