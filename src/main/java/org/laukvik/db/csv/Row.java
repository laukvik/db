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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
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
 * Date created = is.readDate("created");<br>
 * Float percent = is.readFloat("sallary");<br>
 * int sallary = is .readInt("sallary");<br>
 *
 * <li>getBigDecimal
 * <li>getBoolean
 * <li>getDate
 * <li>getDouble
 * <li>getFloat
 * <li>getInt
 * <li>getLong
 * <li>getAsString
 * <li>getURL
 *
 * <li>getTimestamp
 * <li>getByte-
 * <li>getTime -
 *
 * CSV.addRow().add("First");
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class Row implements Serializable {

    private CSV csv;
    private final Map<Column, Object> map;

    public Row() {
        this.map = new TreeMap<>();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        int x = 0;
        for (Column c : map.keySet()) {
            if (x > 0) {
                b.append(",");
            }
            Object o = map.get(c);
            b.append(c.getName()).append("=").append(o);
            x++;
        }
        return b.toString();
    }

    public CSV getCSV() {
        return csv;
    }

    public void setCSV(CSV csv) {
        this.csv = csv;
    }

    public Row update(BinaryColumn column, Byte value) {
        map.put(column, value);
        return this;
    }

    public Row update(TinyIntColumn column, Byte value) {
        map.put(column, value);
        return this;
    }

    public Row update(IntegerColumn column, Integer value) {
        map.put(column, value);
        return this;
    }

    public Row update(BigIntColumn column, Long value) {
        map.put(column, value);
        return this;
    }

    public Row update(FloatColumn column, Double value) {
        map.put(column, value);
        return this;
    }

    public Row update(DoublePrecisionColumn column, Double value) {
        map.put(column, value);
        return this;
    }

    public Row update(BitColumn column, Boolean value) {
        map.put(column, value);
        return this;
    }

    public Row update(CharColumn column, Character value) {
        map.put(column, value);
        return this;
    }

    public Row update(VarCharColumn column, String value) {
        map.put(column, value);
        return this;
    }

    public Row update(LongVarCharColumn column, String value) {
        map.put(column, value);
        return this;
    }

    public Row update(DateColumn column, Date value) {
        map.put(column, value);
        return this;
    }

    public Row update(TimeColumn column, Date value) {
        map.put(column, value);
        return this;
    }

    public Row update(TimestampColumn column, Date value) {
        map.put(column, value);
        return this;
    }

    public Row update(BinaryColumn column, byte[] value) {
        map.put(column, value);
        return this;
    }

    public Row update(VarBinaryColumn column, byte[] value) {
        map.put(column, value);
        return this;
    }

    public Row update(LongVarBinaryColumn column, byte[] value) {
        map.put(column, value);
        return this;
    }

    public Row update(DecimalColumn column, BigDecimal value) {
        map.put(column, value);
        return this;
    }

    public Row update(RealColumn column, Float value) {
        map.put(column, value);
        return this;
    }

    public Row update(NumericColumn column, BigDecimal value) {
        map.put(column, value);
        return this;
    }

    public boolean isNull(Column column) {
        return map.get(column) == null;
    }

    public Object getValue(Column column) {
        return map.get(column);
    }

    public String getAsString(Column column) {
        return map.get(column) + "";
    }

    public String getString(VarCharColumn column) {
        return (String) map.get(column);
    }

    public String getString(LongVarCharColumn column) {
        return (String) map.get(column);
    }

    public Date getDate(TimeColumn column) {
        return (Date) map.get(column);
    }

    public Date getDate(DateColumn column) {
        return (Date) map.get(column);
    }

    public Date getDate(TimestampColumn column) {
        return (Date) map.get(column);
    }

    public Float getFloat(FloatColumn column) {
        return (Float) map.get(column);
    }

    public Integer getInteger(IntegerColumn column) {
        return (Integer) map.get(column);
    }

    public Long getLong(BigIntColumn column) {
        return (Long) map.get(column);
    }

    public char getChar(CharColumn column) {
        return (char) map.get(column);
    }

    public Boolean getBoolean(BitColumn column) {
        return (Boolean) map.get(column);
    }

    public Character getCharacter(CharColumn column) {
        return (Character) map.get(column);
    }

    public Double getDouble(DoublePrecisionColumn column) {
        return (Double) map.get(column);
    }

    public BigDecimal getBigDecimal(DecimalColumn column) {
        return (BigDecimal) map.get(column);
    }

    public byte[] getBytes(LongVarBinaryColumn column) {
        return (byte[]) map.get(column);
    }

    public byte[] getBytes(VarBinaryColumn column) {
        return (byte[]) map.get(column);
    }

    public byte[] getBytes(BinaryColumn column) {
        return (byte[]) map.get(column);
    }

    public BigDecimal getBigDecimal(NumericColumn column) {
        return (BigDecimal) map.get(column);
    }

    public Float getFloat(RealColumn column) {
        return (Float) map.get(column);
    }

    public Byte getByte(TinyIntColumn column) {
        return (Byte) map.get(column);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.csv);
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
        final Row other = (Row) obj;
        return true;
    }

    public void remove(Column column) {
        map.remove(column);
    }

}
