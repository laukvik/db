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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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

    private final BufferedInputStream is;
    private char currentChar;
    private StringBuilder currentValue;
    private StringBuilder rawLine;
    private int lineCounter;
    private MetaData metaData;
    private Row row;
    private final char seperatorChar;

    public CsvReader(InputStream is) throws IOException {
        this(is, Charset.defaultCharset(), CSV.COMMA);
    }

    public CsvReader(InputStream is, Charset charset) throws IOException {
        this(is, charset, CSV.COMMA);
    }

    public CsvReader(InputStream is, Charset charset, char seperatorChar) throws IOException {
        this.seperatorChar = seperatorChar;
        this.is = new BufferedInputStream(is);
        this.lineCounter = 0;
        this.metaData = new MetaData();
        this.metaData.setCharset(charset);
        List<String> columns = parseRow();
        for (String rawColumnName : columns) {
            this.metaData.addColumn(Column.parseColumn(rawColumnName));
        }
    }

    public int getLineCounter() {
        return lineCounter;
    }

    private boolean readRow() throws IOException {
        if (is.available() == 0) {
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

    /**
     *
     * @return @throws IOException
     */
    private List<String> parseRow() throws IOException {
        List<String> values = new ArrayList<>();
        boolean isNextLine = false;

        /* Current value */
        currentValue = new StringBuilder();

        /* the current line */
//        row = new Row();

        /* The raw chars being read */
        rawLine = new StringBuilder();

        boolean isWithinQuote = false;
        int quoteCount = 0;

        /* Read until */
        while (is.available() > 0 && !isNextLine) {

            /* Read next char */
            currentChar = (char) is.read();

            /* Determines whether or not to add char */
            boolean addChar;

            /* Adds the currentValue */
            boolean addValue = false;

            /* Check char */
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
                /*    "Venture ""Extended Edition"""  */
                addChar = true;

                isWithinQuote = true;

                int read = -1;
                while (is.available() > 0) {
                    currentChar = (char) is.read();
                    rawLine.append(currentChar);
                    if (currentChar == CSV.QUOTE) {
                        quoteCount++;
                        break;
                    } else {
                        currentValue.append(currentChar);
                    }
                }

                quoteCount--;

            } else if (currentChar == seperatorChar) { // CSV.COMMA
                addChar = false;
                addValue = true;

                if (isWithinQuote) {
                    currentValue.deleteCharAt(currentValue.length() - 1);
                    isWithinQuote = false;
                }
            } else {
                addChar = true;
            }

//            switch (currentChar) {
//                case CSV.RETURN: /* Found carriage return. Do nothing. */
//
//                    addChar = false;
//                    break;
//
//                case CSV.LINEFEED: /* Found new line symbol */
//
//                    addChar = false;
//                    addValue = true;
//                    isNextLine = true;
//                    if (isWithinQuote) {
//                        currentValue.deleteCharAt(currentValue.length() - 1);
//                        isWithinQuote = false;
//                    }
//                    break;
//
//                case CSV.QUOTE:
//
//                    /*    "Venture ""Extended Edition"""  */
//                    addChar = true;
//
//                    isWithinQuote = true;
//
//                    int read = -1;
//                    while (is.available() > 0) {
//                        currentChar = (char) is.read();
//                        rawLine.append(currentChar);
//                        if (currentChar == CSV.QUOTE) {
//                            quoteCount++;
//                            break;
//                        } else {
//                            currentValue.append(currentChar);
//                        }
//                    }
//
//                    quoteCount--;
//
//                    break;
//
//                case CSV.COMMA:
//
//                    addChar = false;
//                    addValue = true;
//
//                    if (isWithinQuote) {
//                        currentValue.deleteCharAt(currentValue.length() - 1);
//                        isWithinQuote = false;
//                    }
//
//                    break;
//
//                default:
//                    /* Everything else... */
//                    addChar = true;
//                    break;
//            }
            if (addChar) {
                currentValue.append(currentChar);
            }
            if (!isNextLine) {
                rawLine.append(currentChar);
            }

            if (addValue || is.available() == 0) {
                if (is.available() == 0) {
                    if (isWithinQuote) {
                        currentValue.deleteCharAt(currentValue.length() - 1);
                        isWithinQuote = false;
                    }
                }
                values.add(currentValue.toString());
                currentValue = new StringBuilder();
            }
        }
        lineCounter++;
        return values;
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
        is.close();
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
