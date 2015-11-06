package org.laukvik.db.sql.cmd;

import java.util.List;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.sql.Analyzer;
import org.laukvik.db.sql.DatabaseConnection;
import org.laukvik.db.sql.Exporter;

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
