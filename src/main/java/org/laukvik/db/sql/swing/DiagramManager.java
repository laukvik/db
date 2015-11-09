/*
 * Copyright 2015 Laukviks Bedrifter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laukvik.db.sql.swing;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.db.csv.CSV;
import org.laukvik.db.csv.Row;
import org.laukvik.db.csv.io.CsvWriter;
import org.laukvik.db.ddl.IntegerColumn;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.ddl.VarCharColumn;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class DiagramManager {

    private static final Logger LOG = Logger.getLogger(DiagramManager.class.getName());

    public static void write(List<TablePosition> positions, File file) throws IOException {
        LOG.log(Level.FINE, "Writing diagram to file {0}", file.getAbsolutePath());
        CSV csv = new CSV();
        VarCharColumn tableCol = (VarCharColumn) csv.addColumn(new VarCharColumn("table"));
        IntegerColumn xcol = (IntegerColumn) csv.addColumn(new IntegerColumn("x"));
        IntegerColumn ycol = (IntegerColumn) csv.addColumn(new IntegerColumn("y"));
        for (TablePosition tp : positions) {
            Point p = tp.getPoint();
            Table t = tp.getTable();
            csv.addRow().update(tableCol, t.getName()).update(xcol, p.x).update(ycol, p.y);
        }
        CsvWriter w = new CsvWriter(new FileOutputStream(file));
        w.write(csv);
    }

    public static void read(List<TablePosition> positions, File file) throws IOException {
        LOG.log(Level.FINE, "Reading diagram from file {0}", file.getAbsolutePath());
        CSV csv = new CSV();
        csv.read(file);

        VarCharColumn tableCol = (VarCharColumn) csv.getMetaData().getColumn("table");
        IntegerColumn xcol = (IntegerColumn) csv.getMetaData().getColumn("x");
        IntegerColumn ycol = (IntegerColumn) csv.getMetaData().getColumn("y");

        for (int n = 0; n < csv.getRowCount(); n++) {
            Row row = csv.getRow(n);
            String table = row.getString(tableCol);
            int x = row.getInteger(xcol);
            int y = row.getInteger(ycol);

            for (TablePosition tp : positions) {
                if (tp.getTable().getName().equals(table)) {
                    tp.setPoint(new Point(x, y));
                }
            }
        }
    }

}
