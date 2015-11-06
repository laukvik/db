package org.laukvik.db.sql.cmd;

import org.laukvik.db.ddl.View;
import org.laukvik.db.sql.Analyzer;
import org.laukvik.db.sql.DatabaseConnection;

public class ListViews extends SqlCommand {

    public ListViews() {
        super("views", "displays all views");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Analyzer a = new Analyzer();
        for (View v : a.findViews(null, db)) {
            System.out.println(v.getName());
        }
        return 0;
    }
}
