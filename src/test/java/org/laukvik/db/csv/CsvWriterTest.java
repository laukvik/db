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
package org.laukvik.db.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.laukvik.db.csv.io.CsvWriter;
import org.laukvik.db.ddl.VarCharColumn;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
@RunWith(MockitoJUnitRunner.class)
public class CsvWriterTest {

    public static File getResource(String filename) {
        ClassLoader classLoader = CsvWriterTest.class.getClassLoader();
        return new File(classLoader.getResource(filename).getFile());
    }

    @Test
    public void writeAndRead() throws IOException {
        File f = File.createTempFile("CsvWriter", ".csv");
        MetaData md = new MetaData();
        VarCharColumn first = (VarCharColumn) md.addColumn(new VarCharColumn("First"));
        VarCharColumn last = (VarCharColumn) md.addColumn(new VarCharColumn("Last"));
        try (CsvWriter w = new CsvWriter(new FileOutputStream(f), md, Charset.defaultCharset())) {
            //
            w.writeRow(new Row().update(first, "Bill").update(last, "Gates"));
            w.writeRow(new Row().update(first, "Steve").update(last, "Jobs"));
        }
        catch (IOException e) {
            fail("Failed to write CSV file!");
        }
        try {
            CSV csv = new CSV();
            csv.read(f);
            for (int x = 0; x < csv.getMetaData().getColumnCount(); x++) {
                System.out.println(csv.getMetaData().getColumn(x));
            }
            assertEquals("Correct row count", 2, csv.getRowCount());
            assertEquals("First", "First", csv.getMetaData().getColumn(0).getName());
            assertEquals("Last", "Last", csv.getMetaData().getColumn(1).getName());
            assertEquals("Find by row index and index", "Bill", csv.getRow(0).getString(first));
            assertEquals("Find by row index and column name", "Gates", csv.getRow(0).getString(last));
        }
        catch (IOException ex) {
            fail("Failed to read CSV file!");
        }
    }

    @Test
    public void shouldByDigitsOnly() {
        Assert.assertEquals(true, CsvWriter.isDigitsOnly("123"));
    }

    @Test
    public void shouldFail() {
        Assert.assertEquals("Can't start with space", false, CsvWriter.isDigitsOnly(" 123"));
        Assert.assertEquals("Can't end with space", false, CsvWriter.isDigitsOnly("123 "));
        Assert.assertEquals("Can't have space on left and right", false, CsvWriter.isDigitsOnly("123 "));
    }

}
