package org.laukvik.db.sql.cmd;

import java.util.logging.Logger;
import org.laukvik.db.csv.sql.Column;
import org.laukvik.db.csv.sql.ParseException;
import org.laukvik.db.csv.swing.Unique;
import org.laukvik.db.sql.Analyzer;

/**
 * Returns a list of all unique values and the number of instances
 *
 */
public class UniqueValues extends SqlCommand {

    private static final Logger LOG = Logger.getLogger(UniqueValues.class.getName());

    public UniqueValues() {
        super("unique", "table.column", "list all unique values in a column");
    }

    @Override
    public int run(String value) {
        try {
            Column c = Column.parse(value);
            String table = c.getTable().getName();
            String column = c.getName();
            Analyzer a = new Analyzer();
            for (Unique u : a.findUnique(table, column, db)) {
                System.out.format("%d - %s \n", new Object[]{u.getCount(), u.getValue()});
            }
        }
        catch (ParseException ex) {
            return EXCEPTION;
        }
        return SUCCESS;
    }
}
