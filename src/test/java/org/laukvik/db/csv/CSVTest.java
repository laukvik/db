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
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.laukvik.db.csv.io.CsvWriter;
import org.laukvik.db.ddl.VarCharColumn;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class CSVTest {

    @Test
    public void findRows() throws IOException, ParseException {
        CSV csv = new CSV();
        csv.read(getResource("countries.csv"));
        assertEquals(249, csv.getRowCount());
    }

    @Test
    public void iterator() throws IOException, ParseException {
        CSV csv = new CSV();
    }

    @Test
    public void createNew() {
        CSV csv = new CSV();
        VarCharColumn first = csv.addStringColumn("First");
        VarCharColumn last = csv.addStringColumn("Last");
        Row r1 = csv.addRow().update(first, "Bill").update(last, "Gates");
        Row r2 = csv.addRow().update(first, "Steve").update(last, "Jobs");
//        assertEquals("Row1", 2, r1.getValues().size());
//        assertEquals("Row1", 2, r2.getValues().size());
        assertSame("RowCount", csv.getRowCount(), 2);
    }

    @Test
    public void shouldWrite() throws Exception {

        CSV csv = new CSV();
        VarCharColumn first = csv.addStringColumn("First");
        VarCharColumn last = csv.addStringColumn("Last");
        MetaData md = csv.getMetaData();

        assertSame(md.getColumnCount(), 2);

        assertEquals("First should be 0", 0, first.indexOf());
        assertEquals("Last should be 1", 1, last.indexOf());

        Row r1 = csv.addRow().update(first, "Bill").update(last, "Gates");
        Row r2 = csv.addRow().update(first, "Steve").update(last, "Jobs");

        assertSame("RowCount", csv.getRowCount(), 2);
        try {
            csv.write(new CsvWriter(new FileOutputStream(File.createTempFile("ShouldWrite", ".csv")), md, Charset.defaultCharset()));
        }
        catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public static File getResource(String filename) {
        ClassLoader classLoader = CSVTest.class.getClassLoader();
        return new File(classLoader.getResource(filename).getFile());
    }

    @Test
    public void readEscaped() {
        try {

            CSV csv = new CSV();
            csv.read(getResource("escaped.csv"));

            MetaData md = csv.getMetaData();

            VarCharColumn playerName = (VarCharColumn) md.getColumn(0);
            VarCharColumn position = (VarCharColumn) md.getColumn(1);
            VarCharColumn nicknames = (VarCharColumn) md.getColumn(2);
            VarCharColumn yearsActive = (VarCharColumn) md.getColumn(3);

            assertEquals("Player Name", "Player Name", playerName.getName());
            assertEquals("Position", "Position", position.getName());
            assertEquals("Nicknames", "Nicknames", nicknames.getName());
            assertEquals("Years Active", "Years Active", yearsActive.getName());

            assertSame("RowCount", 3, csv.getRowCount());
            assertSame("ColumnCount", 4, md.getColumnCount());

            Row r = csv.getRow(0);

            assertEquals("Skippy", "Skippy Peterson", r.getString(playerName));
            assertEquals("First Base", "First Base", r.getString(position));
            assertEquals("1908-1913", "1908-1913", r.getString(yearsActive));

            String blueDog = "\"Blue Dog\", \"The Magician\"";

            assertEquals("Blue Dog The Magician", "\"Blue Dog\", \"The Magician\"", r.getString(nicknames));

        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readAcid() {
        try {
            CSV csv = new CSV();
            csv.read(getResource("acid.csv"));
            MetaData md = csv.getMetaData();

            assertEquals("Year", "Year", md.getColumn(0).getName());
            assertEquals("Make", "Make", md.getColumn(1).getName());
            assertEquals("Model", "Model", md.getColumn(2).getName());
            assertEquals("Description", "Description", md.getColumn(3).getName());
            assertEquals("Price", "Price", md.getColumn(4).getName());

            assertSame("ColumnCount", 5, md.getColumnCount());
            assertSame("RowCount", 4, csv.getRowCount());

            Row r = csv.getRow(1);

            VarCharColumn model = (VarCharColumn) md.getColumn("Model");

            assertEquals("Venture", "Venture \"Extended Edition\"", r.getString(model));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readEmbeddedCommas() {
        try {
            CSV csv = new CSV();
            File f = getResource("embeddedcommas.csv");
            if (!f.exists()) {
                fail("Could not read test file:");
            }

            csv.read(getResource("embeddedcommas.csv"));
            MetaData md = csv.getMetaData();

            assertEquals("Year", "Year", md.getColumn(0).getName());
            assertEquals("Make", "Make", md.getColumn(1).getName());
            assertEquals("Model", "Model", md.getColumn(2).getName());
            assertEquals("Description", "Description", md.getColumn(3).getName());
            assertEquals("Price", "Price", md.getColumn(4).getName());

            assertSame("ColumnCount", 5, md.getColumnCount());
            assertSame("RowCount", 1, csv.getRowCount());

            Row r = csv.getRow(0);
            System.out.println(r.toString());
            VarCharColumn desc = (VarCharColumn) md.getColumn("Description");

            assertEquals("ac, abs, moon", "ac, abs, moon", r.getString(desc));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readQuoted() {
        try {
            CSV csv = new CSV();
            csv.read(getResource("quoted.csv"));
            MetaData md = csv.getMetaData();
            assertEquals("Lead", "Lead", md.getColumn(0).getName());
            assertEquals("Title", "Title", md.getColumn(1).getName());
            assertEquals("Phone", "Phone", md.getColumn(2).getName());
            assertEquals("Notes", "Notes", md.getColumn(3).getName());

            assertSame("ColumnCount", 4, md.getColumnCount());
            assertSame("RowCount", 3, csv.getRowCount());

            Row r = csv.getRow(0);

            VarCharColumn notes = (VarCharColumn) md.getColumn("Notes");

            assertEquals("Spoke Tuesday, he's interested", r.getString(notes));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readUnqouted() {
        try {
            // Name,Class,Dorm,Room,GPA
            CSV csv = new CSV();
            csv.read(getResource("unquoted.csv"));
            MetaData md = csv.getMetaData();
            assertEquals("Name", "Name", md.getColumn(0).getName());
            assertEquals("Class", "Class", md.getColumn(1).getName());
            assertEquals("Dorm", "Dorm", md.getColumn(2).getName());
            assertEquals("Room", "Room", md.getColumn(3).getName());
            assertEquals("GPA", "GPA", md.getColumn(4).getName());

            assertSame("ColumnCount", 5, md.getColumnCount());
            assertSame("RowCount", 4, csv.getRowCount());

            Row r = csv.getRow(3);

            VarCharColumn col = (VarCharColumn) md.getColumn("GPA");

            assertEquals("GPA", "3.48", r.getString(col));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readInvalid() {
        try {
            CSV csv = new CSV();
            csv.read(getResource("invalid.csv"));
            MetaData md = csv.getMetaData();
            assertEquals("First", "First", md.getColumn(0).getName());
            assertEquals("Last", "Last", md.getColumn(1).getName());
            assertSame("ColumnCount", 2, md.getColumnCount());
        }
        catch (Exception e) {
            assertTrue("Found invalid data", (e instanceof InvalidRowDataException));
        }
    }

    @Test
    public void readEntities() {
        try {
            CSV csv = new CSV();
            csv.read(getResource("person.csv"));
            List<Person> items = CSV.findByClass(Person.class);
            int x = 1;
            for (Person p : items) {
                x++;
            }
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
