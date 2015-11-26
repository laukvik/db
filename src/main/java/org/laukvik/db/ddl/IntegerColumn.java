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
public class IntegerColumn extends Column<Integer> implements AutoIncrementColumn {

    private boolean autoIncrement;

    public IntegerColumn(String name) {
        super(name);
    }

    @Override
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    @Override
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    @Override
    public String asString(Integer value) {
        return value.toString();
    }

    @Override
    public Integer parse(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public int compare(Integer one, Integer another) {
        return one.compareTo(another);
    }

    @Override
    public String toString() {
        return getTable() + "(" + name + ")";
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final IntegerColumn other = (IntegerColumn) obj;
        return true;
    }

    @Override
    public void updateResultSet(int columnIndex, Row row, ResultSet rs) throws SQLException {
        Integer value = row.getInteger(this);
        if (value != null) {
            rs.updateInt(columnIndex, value);
        }
    }

}
