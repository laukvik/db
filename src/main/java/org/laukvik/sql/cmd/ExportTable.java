package org.laukvik.sql.cmd;

import java.util.List;
import org.laukvik.csv.columns.Table;
import org.laukvik.sql.Analyzer;
import org.laukvik.sql.DatabaseConnection;
import org.laukvik.sql.Exporter;

/**
 *
 *
 */
public class ExportTable extends SqlCommand {

    public ExportTable() {
        super("meta", "table", "Exports the table definition");
    }

    @Override
    public int run(DatabaseConnection db, String tableName) {

        Analyzer a = new Analyzer();
        List<Table> tables = a.findTables(null, db);
        Table table = null;
        for (Table t : tables) {
            if (t.getName().equalsIgnoreCase(tableName)) {
                table = t;
            }
        }

        Exporter exporter = new Exporter(db);

//        CSV csv = exporter.createMetaDataCSV(table);
        return SUCCESS;
    }
}
