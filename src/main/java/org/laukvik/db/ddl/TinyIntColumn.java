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
import org.laukvik.db.csv.Row;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class TinyIntColumn extends Column<Byte> implements AutoIncrementColumn {

    private boolean autoIncrement;

    public TinyIntColumn(String name) {
        super(name);
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    @Override
    public String asString(Byte value) {
        return value.toString();
    }

    @Override
    public Byte parse(String value) {
        return Byte.parseByte(value);
    }

    public int compare(Byte one, Byte another) {
        return one.compareTo(another);
    }

    @Override
    public String toString() {
        return name + "(Byte)";
    }

    @Override
    public int hashCode() {
        int hash = 8;
        return hash;
    }

    public String getColumnName() {
        if (isPostgreSQL()) {
            return "Smallint";
        } else {
            return "TinyInt";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TinyIntColumn other = (TinyIntColumn) obj;
        return true;
    }

    @Override
    public void updateResultSet(int columnIndex, Row row, ResultSet rs) throws SQLException {
        Byte value = row.getByte(this);
        if (value != null) {
            rs.updateByte(columnIndex, value);
        }
    }

}
