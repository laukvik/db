package org.laukvik.sql.cmd;

import java.io.IOException;
import java.sql.SQLException;
import org.laukvik.csv.columns.Function;
import org.laukvik.sql.Analyzer;
import org.laukvik.sql.DatabaseConnection;

/**
 * Displays a summary of a function
 *
 */
public class DisplayFunction extends SqlCommand {

    public DisplayFunction() {
        super("function", "functionName", "display information about a function");
    }

    @Override
    public int run(DatabaseConnection db, String value) {
        Analyzer a = new Analyzer();
        Function f = new Function(getParameter());

        try {
            f = a.findFunctionDetails(f, db);
            System.out.println(f.getFunctionSummary());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
