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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.db.csv.CSV;
import org.laukvik.db.csv.MetaData;
import org.laukvik.db.csv.Row;
import org.laukvik.db.ddl.BigIntColumn;
import org.laukvik.db.ddl.BinaryColumn;
import org.laukvik.db.ddl.BitColumn;
import org.laukvik.db.ddl.CharColumn;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.DateColumn;
import org.laukvik.db.ddl.DecimalColumn;
import org.laukvik.db.ddl.DoublePrecisionColumn;
import org.laukvik.db.ddl.FloatColumn;
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
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class CsvReader implements AutoCloseable, Readable {

    private static final Logger LOG = Logger.getLogger(CsvReader.class.getName());

//    private BufferedInputStream is;
//    private char currentChar;
    private StringBuilder currentValue;
    private StringBuilder rawLine;
    private int lineCounter;
    private MetaData metaData;
    private Row row;
    private char seperatorChar;
    private boolean autoDetectSeperator;
    private boolean autoDetectCharset;
    private int bytesRead;
//    private BOM bom;
    //
    private File file;
    private BufferedReader reader;
    private Charset charset;

    /**
     * Reads the CSV file and detects encoding and seperator characters
     * automatically
     *
     * @param file
     * @throws IOException
     */
    public CsvReader(File file) throws IOException {
        this(file, null, null);
    }

    /**
     * Reads the CSV file using the specified charset and automatically detects
     * seperator characters
     *
     * @param file
     * @param charset
     * @throws IOException
     */
    public CsvReader(File file, Charset charset) throws IOException {
        this(file, charset, null);
    }

    /**
     * Reads the CSV file using the specified charset and seperator
     *
     * @param file
     * @param charset
     * @param separator
     * @throws IOException
     */
    public CsvReader(File file, Charset charset, Character separator) throws IOException {
        this.file = file;
        this.autoDetectSeperator = separator == null;
        this.autoDetectCharset = charset == null;
        if (separator != null) {
            this.seperatorChar = separator;
        }
        if (this.autoDetectCharset) {
            // Try to find BOM signature
            BOM bom = BOM.findBom(file);
            if (bom == null) {
                LOG.info("BOM signature not found.");
                this.charset = null;
            } else {
                LOG.log(Level.INFO, "Found BOM signature {0}", bom);
                this.charset = bom.getCharset();
            }
        } else {
            this.charset = charset;
        }
        this.metaData = new MetaData();
        this.metaData.setCharset(this.charset);
        this.lineCounter = 0;
        this.bytesRead = 0;
        if (this.charset == null) {
            reader = Files.newBufferedReader(file.toPath());
        } else {
            reader = Files.newBufferedReader(file.toPath(), this.charset);
        }

        List<String> columns = parseRow();
        for (String rawColumnName : columns) {
            this.metaData.addColumn(Column.parseColumn(rawColumnName));
        }
    }

    /**
     * Parses one row of data
     *
     * @return
     * @throws IOException
     */
    private List<String> parseRow() throws IOException {
        List<String> values = new ArrayList<>();

        boolean isNextLine = false;

        /* Current value */
        currentValue = new StringBuilder();

        /* The raw chars being read */
        rawLine = new StringBuilder();

        boolean isWithinQuote = false;
        int quoteCount = 0;

        /* Read until */
        while (reader.ready() && !isNextLine) {

            // Read next char
            char currentChar = (char) reader.read();

            boolean foundBom = false;
            // Increase number of bytes read
            bytesRead++;

            // Determines whether or not to add char
            boolean addChar = false;

            // Adds the currentValue
            boolean addValue = false;

            // Look for seperator characters in first line
            if (lineCounter == 0 && autoDetectSeperator) {
                if (currentChar == CSV.TAB || currentChar == CSV.SEMINCOLON || currentChar == CSV.PIPE || currentChar == CSV.COMMA) {
                    seperatorChar = currentChar;
                    autoDetectSeperator = false;
                    LOG.log(Level.FINE, "Detected seperator: {0}", seperatorChar);
                }
            }

            // Check char
            if (currentChar == CSV.RETURN) {
                addChar = false;

            } else if (currentChar == CSV.LINEFEED) {

                addChar = false;
                addValue = true;
                isNextLine = true;
                if (isWithinQuote) {
                    currentValue.deleteCharAt(currentValue.length() - 1);
                    isWithinQuote = false;
                }

            } else if (currentChar == CSV.QUOTE) {
                addChar = true;

                isWithinQuote = true;
                while (reader.ready()) {
                    currentChar = (char) reader.read();
                    rawLine.append(currentChar);
                    if (currentChar == CSV.QUOTE) {
                        quoteCount++;
                        break;
                    } else {
                        currentValue.append(currentChar);
                    }
                }

                quoteCount--;

            } else if (currentChar == seperatorChar) {
                addChar = false;
                addValue = true;

                if (isWithinQuote) {
                    currentValue.deleteCharAt(currentValue.length() - 1);
                    isWithinQuote = false;
                }

            } else {
                addChar = true;
            }

            if (addChar) {
                currentValue.append(currentChar);
            }
            if (!isNextLine) {
                rawLine.append(currentChar);
            }

            if (!reader.ready()) {
                addValue = true;
            }

            if (addValue) {
                if (!reader.ready()) {
                    if (isWithinQuote) {
                        currentValue.deleteCharAt(currentValue.length() - 1);
                        isWithinQuote = false;
                    }
                }

//                if (metaData.getCharset() == null) {
//                    values.add(currentValue.toString());
//                } else {
//                    values.add(new String(currentValue.toString().getBytes(), metaData.getCharset()));
//                }
                values.add(currentValue.toString());

                currentValue = new StringBuilder();
            }
        }
        lineCounter++;
        return values;
    }

    /**
     * Reads the next row
     *
     * @return a boolean whether a new row was found
     * @throws IOException
     */
    private boolean readRow() throws IOException {
        if (!reader.ready()) {
            return false;
        }
        row = new Row();
        List<String> values = parseRow();
        if (values.isEmpty()) {
            return false;
        }

        for (int x = 0; x < values.size(); x++) {
            String value = values.get(x);
            if (x >= metaData.getColumnCount()) {
            } else {
                Column c = metaData.getColumn(x);

                if (value == null || value.trim().isEmpty()) {
                    // ---- Char --------------------------------
                } else if (c instanceof CharColumn) {
                    row.update((CharColumn) c, value.charAt(0));
                } else if (c instanceof VarCharColumn) {
                    row.update((VarCharColumn) c, value);
                } else if (c instanceof LongVarCharColumn) {
                    row.update((LongVarCharColumn) c, value);
                } else if (c instanceof BitColumn) {
                    BitColumn bc = (BitColumn) c;
                    row.update(bc, bc.parse(value));

                } else if (c instanceof TinyIntColumn) {
                    TinyIntColumn ic = (TinyIntColumn) c;
                    row.update(ic, ic.parse(value));

                } else if (c instanceof IntegerColumn) {
                    IntegerColumn ic = (IntegerColumn) c;
                    row.update(ic, ic.parse(value));
                } else if (c instanceof BigIntColumn) {
                    BigIntColumn ic = (BigIntColumn) c;
                    row.update(ic, ic.parse(value));

                    // ---- Float --------------------------------
                } else if (c instanceof DoublePrecisionColumn) {
                    DoublePrecisionColumn dc = (DoublePrecisionColumn) c;
                    row.update(dc, dc.parse(value));
                } else if (c instanceof FloatColumn) {
                    FloatColumn fc = (FloatColumn) c;
                    row.update(fc, fc.parse(value));

                } else if (c instanceof DecimalColumn) {
                    DecimalColumn fc = (DecimalColumn) c;
                    row.update(fc, fc.parse(value));

                } else if (c instanceof NumericColumn) {
                    NumericColumn fc = (NumericColumn) c;
                    row.update(fc, fc.parse(value));

                } else if (c instanceof RealColumn) {
                    RealColumn rc = (RealColumn) c;
                    row.update(rc, rc.parse(value));

                    // ---- Date --------------------------------
                } else if (c instanceof DateColumn) {
                    DateColumn dc = (DateColumn) c;
                    row.update(dc, dc.parse(value));
                } else if (c instanceof TimeColumn) {
                    TimeColumn dc = (TimeColumn) c;
                    row.update(dc, dc.parse(value));
                } else if (c instanceof TimestampColumn) {
                    TimestampColumn dc = (TimestampColumn) c;
                    row.update(dc, dc.parse(value));

                    // ---- Binary --------------------------------
                } else if (c instanceof BinaryColumn) {
                    BinaryColumn dc = (BinaryColumn) c;
                    row.update(dc, dc.parse(value));
                } else if (c instanceof VarBinaryColumn) {
                    VarBinaryColumn dc = (VarBinaryColumn) c;
                    row.update(dc, dc.parse(value));
                } else if (c instanceof LongVarBinaryColumn) {
                    LongVarBinaryColumn dc = (LongVarBinaryColumn) c;
                    row.update(dc, dc.parse(value));
                }
            }
        }
        return true;
    }

    public char getSeperatorChar() {
        return seperatorChar;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public int getLineCounter() {
        return lineCounter;
    }

    @Override
    public Row getRow() {
        return row;
    }

    @Override
    public MetaData getMetaData() {
        return metaData;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public boolean hasNext() {
        try {
            return readRow();
        }
        catch (IOException ex) {
            return false;
        }
    }

    @Override
    public Row next() {
        return row;
    }

}
