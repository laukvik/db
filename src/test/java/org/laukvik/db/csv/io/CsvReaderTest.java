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
package org.laukvik.db.csv.io;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.laukvik.db.csv.CSV;
import org.laukvik.db.csv.MetaData;
import org.laukvik.db.csv.Row;
import org.laukvik.db.ddl.BigIntColumn;
import org.laukvik.db.ddl.BinaryColumn;
import org.laukvik.db.ddl.BitColumn;
import org.laukvik.db.ddl.CharColumn;
import org.laukvik.db.ddl.DateColumn;
import org.laukvik.db.ddl.DecimalColumn;
import org.laukvik.db.ddl.DoublePrecisionColumn;
import org.laukvik.db.ddl.IntegerColumn;
import org.laukvik.db.ddl.LongVarBinaryColumn;
import org.laukvik.db.ddl.LongVarCharColumn;
import org.laukvik.db.ddl.NumericColumn;
import org.laukvik.db.ddl.RealColumn;
import org.laukvik.db.ddl.TimeColumn;
import org.laukvik.db.ddl.TimestampColumn;
import org.laukvik.db.ddl.TinyIntColumn;
import org.laukvik.db.ddl.VarBinaryColumn;
import org.laukvik.db.ddl.VarCharColumn;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class CsvReaderTest {

    @Test
    public void autoDetectTab() {
        autoDetectSeperators("seperator_tabbed.txt", CSV.TAB);
    }

    @Test
    public void autoDetectPipe() {
        autoDetectSeperators("seperator_pipe.csv", CSV.PIPE);
    }

    @Test
    public void autoDetectSemiColon() {
        autoDetectSeperators("seperator_semicolon.csv", CSV.SEMINCOLON);
    }

    public void autoDetectSeperators(String fileName, char seperator) {
        try (CsvReader reader = new CsvReader(getResource(fileName), Charset.forName("utf-8"))) {
            int rows = 0;
            assertEquals("seperatorChar", seperator, reader.getSeperatorChar());
            VarCharColumn first = (VarCharColumn) reader.getMetaData().getColumn(0);
            assertEquals("First", first.getName());
            VarCharColumn last = (VarCharColumn) reader.getMetaData().getColumn(1);
            assertEquals("Last", last.getName());
            while (reader.hasNext()) {
                Row r = reader.getRow();
                assertEquals("Morten", r.getString(first));
                assertEquals("Laukvik", r.getString(last));
                rows++;
            }
            assertEquals("Row count", 1, rows);
            assertEquals("Column count", 2, reader.getMetaData().getColumnCount());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readTabDelimited() {
        try (CsvReader reader = new CsvReader(getResource("seperator_tabbed.txt"), Charset.forName("utf-8"), CSV.TAB)) {
            int rows = 0;
            VarCharColumn first = (VarCharColumn) reader.getMetaData().getColumn(0);
            assertEquals("First", first.getName());
            VarCharColumn last = (VarCharColumn) reader.getMetaData().getColumn(1);
            assertEquals("Last", last.getName());
            assertEquals("Column count", 2, reader.getMetaData().getColumnCount());
            while (reader.hasNext()) {
                Row r = reader.getRow();
                assertEquals("Morten", r.getString(first));
                assertEquals("Laukvik", r.getString(last));
                rows++;
            }
            assertEquals("Row count", 1, rows);

        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readSemiColonDelimited() {
        try (CsvReader reader = new CsvReader(getResource("seperator_semicolon.csv"), Charset.forName("utf-8"), CSV.SEMINCOLON)) {
            int rows = 0;
            VarCharColumn first = (VarCharColumn) reader.getMetaData().getColumn(0);
            assertEquals("First", first.getName());
            VarCharColumn last = (VarCharColumn) reader.getMetaData().getColumn(1);
            assertEquals("Last", last.getName());
            while (reader.hasNext()) {
                Row r = reader.getRow();
                assertEquals("Morten", r.getString(first));
                assertEquals("Laukvik", r.getString(last));
                rows++;
            }
            assertEquals("Row count", 1, rows);
            assertEquals("Column count", 2, reader.getMetaData().getColumnCount());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readNumbers() {
        /**
         * Decimal,DoublePrecision,Integer,Numeric,Real,SmallInt,TinyInt,Bit
         * 12.34,1234.5678,123,123456789,123.456789,12,3,0
         */
        try (CsvReader reader = new CsvReader(getResource("datatypes_numbers.csv"), Charset.forName("utf-8"))) {
            int rows = 0;
            DecimalColumn c1 = (DecimalColumn) reader.getMetaData().getColumn(0);
            DoublePrecisionColumn c2 = (DoublePrecisionColumn) reader.getMetaData().getColumn(1);
            IntegerColumn c3 = (IntegerColumn) reader.getMetaData().getColumn(2);
            NumericColumn c4 = (NumericColumn) reader.getMetaData().getColumn(3);
            RealColumn c5 = (RealColumn) reader.getMetaData().getColumn(4);
            TinyIntColumn c7 = (TinyIntColumn) reader.getMetaData().getColumn(6);
            BitColumn c8 = (BitColumn) reader.getMetaData().getColumn(7);
            while (reader.hasNext()) {
                Row r = reader.getRow();
//                assertEquals(new BigDecimal(12.34), r.getBigDecimal(c1));
                assertEquals(new Double(1234.5678), r.getDouble(c2));
                assertEquals(new Integer(123), r.getInteger(c3));
                assertEquals(new BigDecimal(123456789), r.getBigDecimal(c4));
                assertEquals(new Float(123.456789), r.getFloat(c5));
                Short s = 12;
                Byte b = 3;
                assertEquals(b, r.getByte(c7));
                Boolean isTrue = false;
                assertEquals(isTrue, r.getBoolean(c8));

                rows++;
            }
            assertEquals("Row count", 1, rows);
            assertEquals("Column count", 8, reader.getMetaData().getColumnCount());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readBinary() {
        /**
         *
         *
         * "Binary(type=BINARY,size=36)","VarBinaryColumn(type=VarBINARY)","LongVarBinary(type=LongVarBinary)"
         * "VGhlIHJlZCBmb3gganVtcHMgb3ZlciB0aGUgbGF6eSBkb2c=","VGhlIHJlZCBmb3gganVtcHMgb3ZlciB0aGUgbGF6eSBkb2c=","VGhlIHJlZCBmb3gganVtcHMgb3ZlciB0aGUgbGF6eSBkb2c="
         *
         */
        try (CsvReader reader = new CsvReader(getResource("datatypes_binary.csv"), Charset.forName("utf-8"))) {
            int rows = 0;
            BinaryColumn c1 = (BinaryColumn) reader.getMetaData().getColumn(0);
            VarBinaryColumn c2 = (VarBinaryColumn) reader.getMetaData().getColumn(1);
            LongVarBinaryColumn c3 = (LongVarBinaryColumn) reader.getMetaData().getColumn(2);
            while (reader.hasNext()) {
                Row r = reader.getRow();

                rows++;
            }
            assertEquals("Row count", 1, rows);
            assertEquals("Column count", 3, reader.getMetaData().getColumnCount());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readText() {
        /**
         *
         * "Char(type=char)","VarChar(type=varChar,size=10)","LongVarchar(type=varChar)"
         * B,"abcdefghij","The red fox jumps over the lazy dog"
         *
         */
        try (CsvReader reader = new CsvReader(getResource("datatypes_text.csv"), Charset.forName("utf-8"))) {
            int rows = 0;
            CharColumn c1 = (CharColumn) reader.getMetaData().getColumn(0);
            VarCharColumn c2 = (VarCharColumn) reader.getMetaData().getColumn(1);
            LongVarCharColumn c3 = (LongVarCharColumn) reader.getMetaData().getColumn(2);
            while (reader.hasNext()) {
                Row r = reader.getRow();

                assertEquals('B', r.getChar(c1));

                assertEquals("abcdefghij", r.getString(c2));

                assertEquals("The red fox jumps over the lazy dog", r.getString(c3));

                rows++;
            }
            assertEquals("Row count", 1, rows);
            assertEquals("Column count", 3, reader.getMetaData().getColumnCount());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readDates() {
        try (CsvReader reader = new CsvReader(getResource("datatypes_dates.csv"), Charset.forName("utf-8"))) {
            int rows = 0;
            DateColumn dc1 = (DateColumn) reader.getMetaData().getColumn(0);
            TimeColumn dc2 = (TimeColumn) reader.getMetaData().getColumn(1);
            TimestampColumn dc3 = (TimestampColumn) reader.getMetaData().getColumn(2);
            while (reader.hasNext()) {
                Row r = reader.getRow();

                Date r1 = dc1.parse("2011-10-13 16:26:45.0");
                Date d1 = r.getDate(dc1);
                assertEquals(r1, d1);

                Date r2 = dc2.parse("2012-03-13 11:21:15.0");
                Date d2 = r.getDate(dc2);
                assertEquals(r2, d2);

                Date r3 = dc3.parse("2009-03-21 17:14:15.3");
                Date d3 = r.getDate(dc3);
                assertEquals(r3, d3);

                rows++;
            }
            assertEquals("Row count", 1, rows);
            assertEquals("Column count", 3, reader.getMetaData().getColumnCount());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public void readFile(String filename, int requiredColumns, int requiredRows, String charset) {
        try (CsvReader reader = new CsvReader(getResource(filename), Charset.forName(charset))) {
            int rows = 0;
            while (reader.hasNext()) {
                Row r = reader.getRow();
//                assertSame("Column count for row " + (rows + 1) + ": ", requiredColumns, r.getValues().size());
                rows++;
            }
            assertEquals("Row count", rows, requiredRows);
            assertEquals("Column count", reader.getMetaData().getColumnCount(), requiredColumns);
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void readAcid() {
        readFile("acid.csv", 5, 4, "us-ascii");
    }

    @Test
    public void readEmbeddedCommas() {
        readFile("embeddedcommas.csv", 5, 1, "us-ascii");
    }

    @Test
    public void readEscaped() {
        readFile("escaped.csv", 4, 3, "utf-8");
    }

    @Test
    public void readQuoted() {
        readFile("quoted.csv", 4, 3, "us-ascii");
    }

    @Test
    public void readUnquoted() {
        readFile("unquoted.csv", 5, 4, "us-ascii");
    }

    @Test
    public void readActivity() {
        /*
         *  "ID(type=BIGINT,primaryKey=true,allowNulls=false)",
         *  "name(type=VARCHAR,size=45,allowNulls=false)",
         *  "description(type=VARCHAR,size=128)",
         *  "created(type=TIMESTAMP)",
         *  "updated(type=TIMESTAMP)"
         */
        try (CsvReader reader = new CsvReader(getResource("activity.csv"), Charset.forName("utf-8"))) {
            MetaData metaData = reader.getMetaData();

            assertEquals("ID", metaData.getColumn(0).getName());
            assertEquals(BigIntColumn.class, metaData.getColumn(0).getClass());

            assertEquals("name", metaData.getColumn(1).getName());
            assertEquals(VarCharColumn.class, metaData.getColumn(1).getClass());
            assertEquals(45, ((VarCharColumn) metaData.getColumn(1)).getSize());

            assertEquals("description", metaData.getColumn(2).getName());

            assertEquals("created", metaData.getColumn(3).getName());
            assertEquals(TimestampColumn.class, metaData.getColumn(3).getClass());

            assertEquals("updated", metaData.getColumn(4).getName());
            assertEquals(TimestampColumn.class, metaData.getColumn(4).getClass());

        }
        catch (IOException e) {
            fail(e.getMessage());
        }

    }

    public static File getResource(String filename) {
        ClassLoader classLoader = CsvReaderTest.class.getClassLoader();
        return new File(classLoader.getResource(filename).getFile());
    }

    @Test
    public void readWithIterator() {
        String filename = "acid.csv";
        String charset = "utf-8";
        try (CsvReader reader = new CsvReader(getResource(filename), Charset.forName(charset))) {
            // Find columns
            VarCharColumn col1 = (VarCharColumn) reader.getMetaData().getColumn(0);
            VarCharColumn col2 = (VarCharColumn) reader.getMetaData().getColumn(1);
            VarCharColumn col3 = (VarCharColumn) reader.getMetaData().getColumn(2);
            VarCharColumn col4 = (VarCharColumn) reader.getMetaData().getColumn(3);

            while (reader.hasNext()) {
                Row row = reader.next();
            }

//            if (reader.hasNext()) {
//                Row row = reader.next();
//                assertEquals("Year", "1996", row.getString(col));
//            } else {
//                fail("Next row not found");
//            }
        }
        catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void readCharset() {
        String filename = "charset.csv";
        String charset = "ISO-8859-1";
        String norwegian = "Norwegian æøå and ÆØÅ";
        try (CsvReader r = new CsvReader(getResource(filename), Charset.forName(charset))) {
            while (r.hasNext()) {
                Row row = r.next();
                //assertEquals("Norwegian chars", norwegian, row.getAsString("text"));
            }
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    //@Test
    public void readRows() {
        String filename = "countries.csv";
        String charset = "utf-8";
        try (CsvReader r = new CsvReader(getResource(filename), Charset.forName(charset))) {
            VarCharColumn col = (VarCharColumn) r.getMetaData().getColumn(0);
            int x = 1;
            while (r.hasNext()) {
                r.getRow().getString(col);
            }
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
