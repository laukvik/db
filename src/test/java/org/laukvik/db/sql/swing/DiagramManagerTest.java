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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.laukvik.db.csv.CSV;
import org.laukvik.db.ddl.IntegerColumn;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.ddl.VarCharColumn;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class DiagramManagerTest {

    @Test
    public void writeDiagram() throws IOException {
        Table t1 = new Table("Employee");
        Point p1 = new Point(50, 60);
        TablePosition tp1 = new TablePosition(t1, p1);
        List<TablePosition> positions = new ArrayList<>();
        File file = File.createTempFile("employee-test", ".csv");
        file = new File("/Users/morten/Desktop/employee.csv");
        DiagramManager.write(positions, file);

//        System.out.println(file.getAbsolutePath());
        CSV csv = new CSV();
        csv.read(file);
        Assert.assertEquals(3, csv.getMetaData().getColumnCount());

        VarCharColumn tablec = (VarCharColumn) csv.getMetaData().getColumn("table");
        IntegerColumn icx = (IntegerColumn) csv.getMetaData().getColumn("x");
        IntegerColumn icy = (IntegerColumn) csv.getMetaData().getColumn("y");

        Assert.assertEquals("table", csv.getMetaData().getColumn(0).getName());
        Assert.assertEquals("x", csv.getMetaData().getColumn(1).getName());
        Assert.assertEquals("y", csv.getMetaData().getColumn(2).getName());

    }

}
