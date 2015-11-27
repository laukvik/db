package org.laukvik.db.sql.cmd;

import java.util.Map;
import java.util.logging.Logger;
import org.laukvik.db.csv.swing.Unique;
import org.laukvik.db.sql.Analyzer;

/**
 * Starts the app
 *
 */
public class TableRowCount extends SqlCommand {

    private static final Logger LOG = Logger.getLogger(TableRowCount.class.getName());

    public TableRowCount() {
        super("rows", "shows the row count in each table");
    }

    @Override
    public int run(String value, Map<String, String> props) {
        Analyzer a = new Analyzer();
        for (Unique u : a.findRowCount(db)) {
            System.out.println(u.getValue() + " " + u.getCount());
        }
        return SUCCESS;
    }
}
