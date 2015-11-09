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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.laukvik.db.ddl.Column;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class MetaData implements Serializable {

    private final List<Column> columns;
    private Charset charset;

    public MetaData() {
        charset = Charset.defaultCharset();
        columns = new ArrayList<>();
    }

    public Column getColumn(String name) {
        for (Column c : columns) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        throw new ColumnNotFoundException(name);
    }

    public Column getColumn(int columnIndex) {
        return columns.get(columnIndex);
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public int getColumnCount() {
        return columns.size();
    }

    /**
     *
     * columnName(DataType=option)
     *
     * @param columnName
     * @return
     */
    public Column addColumn(String columnName) {
        return addColumn(Column.parseName(columnName));
    }

    public Column addColumn(Column column) {
        column.setMetaData(this);
        columns.add(column);
        return column;
    }

    public void removeColumn(Column column) {
        column.setMetaData(null);
        columns.remove(column);
    }

    public int indexOf(Column column) {
        return columns.indexOf(column);
    }

    public Column insertColumn(Column column, int index) {
        column.setMetaData(this);
        columns.add(index, column);
        return column;
    }

//    public void addColumn(String name, int columnIndex) {
////        columns.add(new StringColumn(name), name);
//        throw new IllegalArgumentException("Not implemented yet");
//    }
    public void removeColumn(int columnIndex) {
        columns.get(columnIndex).setMetaData(null);
        columns.remove(columnIndex);
    }

    public List<Column> findForeignKeys() {
        List<Column> cols = new ArrayList<>();
        for (Column c : columns) {
            if (c.getForeignKey() != null) {
                cols.add(c);
            }
        }
        return cols;
    }

    public List<Column> findPrimaryKeys() {
        List<Column> cols = new ArrayList<>();
        for (Column c : columns) {
            if (c.isPrimaryKey()) {
                cols.add(c);
            }
        }
        return cols;
    }

}
