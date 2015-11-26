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
package org.laukvik.db.ddl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.laukvik.db.csv.Row;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class TimestampColumn extends Column<Date> {

    private DateFormat dateFormat;
    private String format;

    public TimestampColumn(String name, String format) {
        super(name);
        setFormat(format);
    }

    public final void setFormat(String format) {
        this.format = format;
        this.dateFormat = new SimpleDateFormat(format);
    }

    public TimestampColumn(String name) {
        this(name, "yyyy.MM.dd hh:mm:ss");
    }

    public String getFormat() {
        return format;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public boolean isYear(Date d, int year) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(d);
        return c.get(Calendar.YEAR) == year;
    }

    public boolean isMonth(Date d, int month) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(d);
        return c.get(Calendar.MONTH) == month;
    }

    public String asString(Date value) {
        return dateFormat.format(value);
    }

    @Override
    public Date parse(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            Date d = dateFormat.parse(value);
            return d;
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public int compare(Date one, Date another) {
        return one.compareTo(another);
    }

    @Override
    public String toString() {
        return name + "(Date)";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimestampColumn other = (TimestampColumn) obj;
        return true;
    }

    @Override
    public void updateResultSet(int columnIndex, Row row, ResultSet rs) throws SQLException {
        Date value = row.getDate(this);
        if (value != null) {
            rs.updateTimestamp(columnIndex, new java.sql.Timestamp(value.getTime()));
        }
    }

}
