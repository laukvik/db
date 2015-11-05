package org.laukvik.sql.cmd;

import org.laukvik.csv.columns.View;
import org.laukvik.sql.Analyzer;
import org.laukvik.sql.DatabaseConnection;

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
