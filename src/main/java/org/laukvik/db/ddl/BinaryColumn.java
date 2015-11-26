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
import java.util.Arrays;
import org.laukvik.db.csv.Row;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class BinaryColumn extends Column<byte[]> {

    public BinaryColumn(String name) {
        super(name);
    }

    @Override
    public String asString(byte[] value) {
        return new String(value);
    }

    @Override
    public byte[] parse(String value) {
        return value.getBytes();
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
        final BinaryColumn other = (BinaryColumn) obj;
        return true;
    }

    /**
     * @todo implement sorting for bytecolumn
     *
     * @param one
     * @param another
     * @return
     */
    public int compare(byte[] one, byte[] another) {
        if (Arrays.equals(one, another)) {
            return 0;
        }
        return 1;
    }

    @Override
    public String toString() {
        return name + "(Byte)";
    }

    @Override
    public void updateResultSet(int columnIndex, Row row, ResultSet rs) throws SQLException {
        byte[] value = row.getBytes(this);
        if (value != null) {
            rs.updateBytes(columnIndex, value);
        }
    }

}
