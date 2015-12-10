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
import org.laukvik.db.DatabaseType;
import org.laukvik.db.ddl.AutoIncrementColumn;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.ForeignKey;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.sql.DatabaseConnection;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class MetaData implements Serializable {

    private final List<Column> columns;
    private Charset charset;
    private Table table;
    private Character seperator;

    public MetaData() {
        charset = Charset.defaultCharset();
        columns = new ArrayList<>();
        table = null;
    }

    public Character getSeperator() {
        return seperator;
    }

    public void setSeperator(Character seperator) {
        this.seperator = seperator;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
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

    public boolean isPostInstallRequired() {
        boolean required = false;
        for (Column c : columns) {
            if (c.getForeignKey() != null) {
                required = true;
            }
        }
        return required;
    }

    public String getInstallationScript(String name) {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE ");
        b.append(name);
        b.append(" (");

        for (int x = 0; x < getColumnCount(); x++) {
            Column c = getColumn(x);
            b.append(x > 0 ? "," : "");
            b.append("\n\t");
            b.append(c.getName());
            b.append("\t");
            b.append(c.getDDL());
            /*
             if (c.getForeignKey() == null){

             } else {
             b.append(" REFERENCES ");
             b.append( c.getForeignKey().findTableByName() );
             b.append("(");
             b.append( c.getForeignKey().getColumn() );
             b.append(")");
             }
             */
            if (c.getDefaultValue() != null) {
                b.append(" DEFAULT ");
                b.append(c.getDefaultValue());
                b.append("");
            }
//            if (c.isAutoIncrement()) {
//                //b.append(" AUTOINCREMENT");
//            }

        }

        List<Column> primaryKeys = findPrimaryKeys();
        if (!primaryKeys.isEmpty()) {
            b.append(",\n\tPRIMARY KEY(");
            for (int x = 0; x < primaryKeys.size(); x++) {
                Column c = primaryKeys.get(x);
                if (x > 0) {
                    b.append(",");
                }
                b.append(c.getName());
            }
            b.append(")");
        }

        b.append("\n");
        b.append(");");
        b.append("\n");
        return b.toString();
    }

    private List<String> getConvert(String table, String column) {
        List<String> items = new ArrayList<>();
        items.add("CREATE SEQUENCE " + table + "_" + column + "_seq;");
//        items.add("CREATE TABLE " + table + " ( " + column + " integer NOT NULL DEFAULT nextval('" + table + "_" + column + "_seq'));");
        items.add("ALTER TABLE " + table + " ALTER COLUMN " + column + " SET DEFAULT nextval('" + table + "_" + column + "_seq')" + ";");
        items.add("ALTER SEQUENCE " + table + "_" + column + "_seq OWNED BY " + table + "." + column + ";");
        return items;
    }

    public List<String> getAutoNumberScript(String name, DatabaseConnection db) {
        List<String> list = new ArrayList<>();
        DatabaseType dbType = db.getDatabaseType();
        if (dbType == null || name == null) {
            return list;
        }
        for (int x = 0; x < getColumnCount(); x++) {
            Column c = getColumn(x);
            if (c instanceof AutoIncrementColumn) {
                AutoIncrementColumn aic = (AutoIncrementColumn) c;
                if (aic.isAutoIncrement()) {
                    switch (dbType) {
                        case MySQL:
                            list.add("ALTER TABLE " + name + " MODIFY COLUMN " + c.getName() + " " + c.getColumnName() + " auto_increment;");
                            break;
                        case PostgreSQL:
//                            list.add("ALTER TABLE " + name + " ALTER COLUMN " + c.getName() + " TYPE SERIAL;");
                            list.addAll(getConvert(name, c.getName()));
                            break;
                    }
                } else {

                }
            }
        }
        return list;
    }

    /**
     * Creates
     *
     * @param db
     * @return
     */
    public List<String> getConstraintScript(String table, DatabaseConnection db) {
        List<String> list = new ArrayList<>();
        for (int x = 0; x < getColumnCount(); x++) {
            Column c = getColumn(x);
            if (c.getForeignKey() != null) {
                ForeignKey fk = c.getForeignKey();
                list.add("ALTER TABLE " + table + " ADD FOREIGN KEY (" + c.getName() + ") REFERENCES " + fk.getDDL() + ";");
            }
        }
        return list;
    }

}
