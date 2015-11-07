/*
 * Copyright (C) 2014 morten
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.laukvik.db.ddl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.laukvik.db.csv.MetaData;
import org.laukvik.db.sql.DatabaseConnection;

/**
 *
 * @author morten
 */
public class Table implements Sqlable {

    private final String name;
    private Schema schema;
    private MetaData metaData;

    public Table(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cant be null or empty!");
        }
        this.name = name;
        this.metaData = new MetaData();
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

//    public List<Column> getColumns() {
//        return columns;
//    }
    public void setPrimaryKey(String columnName, boolean isEnabled) {
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

//    public void addColumn(Column c) {
//        c.setTable(this);
//        columns.add(c);
//    }
//    public List<Column> findForeignKeys() {
//        List<Column> cols = new ArrayList<>();
//        for (Column c : columns) {
//            if (c.getForeignKey() != null) {
//                cols.add(c);
//            }
//        }
//        return cols;
//    }
//
//    public List<Column> findPrimaryKeys() {
//        List<Column> cols = new ArrayList<>();
//        for (Column c : columns) {
//            if (c.isPrimaryKey()) {
//                cols.add(c);
//            }
//        }
//        return cols;
//    }
//    public Column findColumnByName(String name) {
//        for (Column c : columns) {
//            if (c.getName().equalsIgnoreCase(name)) {
//                return c;
//            }
//        }
//        return null;
//    }
    public String getInsertSQL(ResultSet rs) throws SQLException {
        StringBuilder b = new StringBuilder();
        b.append("INSERT INTO " + name + "(");
        // Names

        for (int x = 0; x < metaData.getColumnCount(); x++) {
            Column c = metaData.getColumn(x);
            b.append(x > 0 ? "," : "");
            b.append(c.getName());
        }

        b.append(") VALUES (");
        // Values

        for (int x = 0; x < metaData.getColumnCount(); x++) {
            Column c = metaData.getColumn(x);
            b.append(x > 0 ? "," : "");

            Object value = rs.getObject(x + 1);

            if (rs.wasNull()) {
                b.append("NULL");

            } else {
                b.append(c.getFormatted(value));
            }
        }

        b.append(");");
        return b.toString();
    }

    public String getSelectTable() {
        return "SELECT * FROM " + name + " ORDER BY " + metaData.getColumn(0).getName() + " ASC";
    }

    public List<String> getPostConstraintScript(DatabaseConnection db) {
        List<String> list = new ArrayList<>();
        for (int x = 0; x < metaData.getColumnCount(); x++) {
            Column c = metaData.getColumn(x);
            if (c.getForeignKey() != null) {
                ForeignKey fk = c.getForeignKey();
                list.add("ALTER TABLE " + c.getTable().getName() + " ADD FOREIGN KEY (" + c.getName() + ") REFERENCES " + fk.getDDL() + ";");
            }
        }
        return list;
    }

    public List<String> getPostAutoNumberScript(DatabaseConnection db) {
        List<String> list = new ArrayList<>();
//        for (Column c : columns) {
//            if (c.isAutoIncrement()) {
//
//                if (db.getDriver() == null) {
//                    // Do nothing
//                } else if (db.getDriver().equalsIgnoreCase("postgres")) {
//                    //b.append("ALTER TABLE " + name + " ALTER COLUMN "+ c.getName() +" TYPE SERIAL;");
//
//                } else if (db.getDriver().equalsIgnoreCase("mysql")) {
//                    //b.append( "ALTER TABLE " + name + " MODIFY COLUMN " + c.getName() + " " + c.getColumnName() + " auto_increment;" );
//                    list.add("ALTER TABLE " + name + " MODIFY COLUMN " + c.getName() + " " + c.getColumnName() + " auto_increment;");
//                } else {
//
//                }
//            }
//        }
        return list;
    }

    public String getDDL() {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE ");
        b.append(name);
        b.append(" (");

        for (int x = 0; x < metaData.getColumnCount(); x++) {
            Column c = metaData.getColumn(x);
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
            x++;
        }

        List<Column> primaryKeys = getMetaData().findPrimaryKeys();
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

    /**
     * Returns whether a script is necessary to run before completion
     *
     * @return
     */
    public boolean isPostInstallRequired() {
        boolean required = false;
        for (int x = 0; x < metaData.getColumnCount(); x++) {
            Column c = metaData.getColumn(x);
//            if (c.isAutoIncrement()) {
//                required = true;
//            }
            if (c.getForeignKey() != null) {
                required = true;
            }
        }
        return required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Table table = (Table) o;

        if (!name.equals(table.name)) {
            return false;
        }
        return !(schema != null ? !schema.equals(table.schema) : table.schema != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        return result;
    }
}
