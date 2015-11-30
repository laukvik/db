package org.laukvik.db.cmd;

import java.util.Map;
import java.util.logging.Logger;
import org.laukvik.db.parser.Column;
import org.laukvik.db.parser.ParseException;
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
    public int run(String value, Map<String, String> props) {
        try {
            Column c = Column.parse(value);
            String table = c.getTable().getName();
            String column = c.getName();
            Analyzer a = new Analyzer();
            for (org.laukvik.db.csv.swing.Unique u : a.findUnique(table, column, db)) {
                System.out.format("%d - %s \n", u.getCount(), u.getValue());
            }
        }
        catch (ParseException ex) {
            return EXCEPTION;
        }
        return SUCCESS;
    }
}
