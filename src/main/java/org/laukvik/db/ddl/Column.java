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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.laukvik.db.csv.MetaData;
import org.laukvik.db.csv.Row;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 * @param <T>
 */
public abstract class Column<T> implements Comparable {

    private MetaData metaData;
    private boolean primaryKey;
    private boolean allowNulls;
    private ForeignKey foreignKey;
    private String defaultValue;
    private Table table;
    String name;
    private String comments;

    public Column(String name) {
        setName(name);
    }

    public static Column parse(int columnType, String name) {
        switch (columnType) {
            case java.sql.Types.BIT:
                return new BitColumn(name);

            case java.sql.Types.TINYINT:
                return new TinyIntColumn(name);
            case java.sql.Types.INTEGER:
                return new IntegerColumn(name);
            case java.sql.Types.BIGINT:
                return new BigIntColumn(name);

            case java.sql.Types.BINARY:
                return new BinaryColumn(name);
            case java.sql.Types.VARBINARY:
                return new VarBinaryColumn(name);
            case java.sql.Types.LONGVARBINARY:
                return new LongVarBinaryColumn(name);

            case java.sql.Types.CHAR:
                return new CharColumn(name);
            case java.sql.Types.VARCHAR:
                return new VarCharColumn(name);
            case java.sql.Types.LONGVARCHAR:
                return new LongVarCharColumn(name);

            case java.sql.Types.NUMERIC:
                return new NumericColumn(name);
            case java.sql.Types.DECIMAL:
                return new DecimalColumn(name);

            case java.sql.Types.FLOAT:
                return new FloatColumn(name);
            case java.sql.Types.REAL:
                return new RealColumn(name);
            case java.sql.Types.DOUBLE:
                return new DoublePrecisionColumn(name);

            case java.sql.Types.DATE:
                return new DateColumn(name);
            case java.sql.Types.TIME:
                return new TimeColumn(name);
            case java.sql.Types.TIMESTAMP:
                return new TimestampColumn(name);

            case java.sql.Types.OTHER:
                return new OtherColumn(name);
        }
        throw new IllegalArgumentException("ColumnType: " + columnType);
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cant be empty");
        }
        this.name = name;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public abstract String asString(T value);

    public abstract T parse(String value);

    public abstract int compare(T one, T another);

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public ForeignKey getForeignKey() {
        return foreignKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setForeignKey(ForeignKey foreignKey) {
        this.foreignKey = foreignKey;
    }

    public boolean isAllowNulls() {
        return allowNulls;
    }

    public void setAllowNulls(boolean allowNulls) {
        this.allowNulls = allowNulls;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int indexOf() {
        return metaData.indexOf(this);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Column) {
            Column c = (Column) o;
            return getName().compareTo(c.getName());
        }
        return -1;
    }

    /**
     * "President(type=VARCHAR,primaryKey=true,increment=true,foreignKey=null,default='',comments='')"
     *
     * @param columnWithMeta
     * @return
     */
    public static Column parseColumn(String columnWithMeta) {

        String columnNameWithOptionalMetaData = columnWithMeta.trim();
        if (columnNameWithOptionalMetaData.startsWith("\"")) {
            columnNameWithOptionalMetaData = columnNameWithOptionalMetaData.substring(1);
        }
        if (columnNameWithOptionalMetaData.endsWith("\"")) {
            columnNameWithOptionalMetaData = columnNameWithOptionalMetaData.substring(0, columnNameWithOptionalMetaData.length() - 1);
        }

        /* Extract extra information about the column*/
        String columnName = null;
        // Variables for meta values
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        // Look for metadata in column headers
        int firstIndex = columnNameWithOptionalMetaData.indexOf("(");
        if (firstIndex == -1) {
            // No extra information
            columnName = columnNameWithOptionalMetaData;
        } else {
            // Found extra information
            int lastIndex = columnNameWithOptionalMetaData.lastIndexOf(")");
            columnName = columnNameWithOptionalMetaData.substring(0, firstIndex);
            if (lastIndex == -1) {
            } else {
                // String with metadata
                String extraDetails = columnNameWithOptionalMetaData.substring(firstIndex + 1, lastIndex);

                String[] keyValues;
                if (extraDetails.contains(",")) {
                    keyValues = extraDetails.split(",");
                } else {
                    keyValues = new String[]{extraDetails};
                }
                // Extract all key/value pairs from metadata
                for (String keyValue : keyValues) {
                    if (keyValue.contains("=")) {
                        String[] arr = keyValue.split("=");
                        String key = arr[0];
                        keys.add(key.trim());

                        if (arr.length < 2) {
                            values.add("");
                        } else {
                            values.add(arr[1].trim());
                        }

                    } else {
                        keys.add(keyValue.trim());
                        values.add("");
                    }
                }

            }
        }
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalColumnDefinitionException(columnName);
        } else {
            System.out.println(columnName);
        }

        // Find dataType before continuing
        String dataType = getValue("type", keys, values);
        // When using MetaData the type attribute is required otherwise it
        // will become a VarCharColumn
        if (dataType == null) {
            return new VarCharColumn(columnWithMeta);
        }

        boolean allowsNull = true;
        {
            String allowNullValue = getValue("allowNulls", keys, values);
            if (allowNullValue != null) {
                if (allowNullValue.equalsIgnoreCase("true")) {
                    allowsNull = true;
                } else if (allowNullValue.equalsIgnoreCase("false")) {
                    allowsNull = false;
                } else {
                    throw new IllegalColumnDefinitionException("allowNulls  cant be " + allowNullValue);
                }
            }
        }

        boolean primaryKey = false;
        {
            String pkValue = getValue("primaryKey", keys, values);
            if (pkValue != null) {
                if (pkValue.equalsIgnoreCase("true")) {
                    primaryKey = true;
                } else if (pkValue.equalsIgnoreCase("false")) {
                    primaryKey = false;
                } else {
                    throw new IllegalColumnDefinitionException("primaryKey cant be " + pkValue);
                }
            }
        }

        ForeignKey foreignKey = null;
        {
            String fkValue = getValue("foreignKey", keys, values);
            if (fkValue == null || fkValue.trim().isEmpty()) {

            } else {
                foreignKey = ForeignKey.parse(fkValue);
            }
        }

        String defaultValue = getValue("default", keys, values);
        String autoIncremented = getValue("autoIncrement", keys, values);
        boolean autoIncrement = false;
        if (autoIncremented == null || autoIncremented.trim().isEmpty()) {
            // Empty
        } else {
            if (autoIncremented.equalsIgnoreCase("true")) {
                autoIncrement = true;
            } else if (autoIncremented.equalsIgnoreCase("false")) {
                autoIncrement = false;
            } else {
                throw new IllegalColumnDefinitionException("AutoNumber cant be '" + autoIncremented + "'");
            }
        }

        // Find all key pairs
        String s = dataType.toUpperCase();
        if (s.equalsIgnoreCase("INTEGER")) {
            IntegerColumn c = new IntegerColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            c.setAutoIncrement(autoIncrement);
            return c;
        } else if (s.equalsIgnoreCase("TINYINT")) {
            TinyIntColumn c = new TinyIntColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            c.setAutoIncrement(autoIncrement);
            return c;
        } else if (s.equalsIgnoreCase("BIGINT")) {
            BigIntColumn c = new BigIntColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            c.setAutoIncrement(autoIncrement);
            return c;
        } else if (s.equalsIgnoreCase("FLOAT")) {
            FloatColumn c = new FloatColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("DOUBLE") || s.equalsIgnoreCase("DOUBLE PRECISION")) {
            DoublePrecisionColumn c = new DoublePrecisionColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("BIT")) {
            BitColumn c = new BitColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("DATE")) {
            String format = getValue("format", keys, values);
            if (format == null || format.trim().isEmpty()) {
                throw new IllegalColumnDefinitionException("Format cant be empty!");
            }
            try {
                SimpleDateFormat f = new SimpleDateFormat(format);
            }
            catch (Exception e) {
                throw new IllegalColumnDefinitionException("Format cant be " + format + "!");
            }
            DateColumn c = new DateColumn(columnName, format);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;

        } else if (s.equalsIgnoreCase("TIME")) {
            String format = getValue("format", keys, values);
            if (format == null || format.trim().isEmpty()) {
                throw new IllegalColumnDefinitionException("Format cant be empty!");
            }
            try {
                SimpleDateFormat f = new SimpleDateFormat(format);
            }
            catch (Exception e) {
                throw new IllegalColumnDefinitionException("Format cant be " + format + "!");
            }
            TimeColumn c = new TimeColumn(columnName, format);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;

        } else if (s.equalsIgnoreCase("TIMESTAMP")) {
            String format = getValue("format", keys, values);
            if (format == null || format.trim().isEmpty()) {
                throw new IllegalColumnDefinitionException("Format cant be empty!");
            }
            try {
                SimpleDateFormat f = new SimpleDateFormat(format);
            }
            catch (Exception e) {
                throw new IllegalColumnDefinitionException("Format cant be " + format + "!");
            }
            TimestampColumn c = new TimestampColumn(columnName, format);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;

        } else if (s.equalsIgnoreCase("BIGDECIMAL")) {
            NumericColumn c = new NumericColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;

        } else if (s.equalsIgnoreCase("Real")) {
            RealColumn c = new RealColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;

        } else if (s.startsWith("VARCHAR")) {
            VarCharColumn c = new VarCharColumn(columnName);
            c.setAllowNulls(allowsNull);
            c.setPrimaryKey(primaryKey);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);

            String size = getValue("size", keys, values);
            if (size == null || size.trim().isEmpty()) {
            } else {
                try {
                    Integer i = Integer.parseInt(size);
                    c.setSize(i);
                }
                catch (Exception e) {
                    throw new IllegalColumnDefinitionException("Column " + columnName + " has invalid size '" + size + "'");
                }
            }

            return c;
        } else if (s.equalsIgnoreCase("Binary")) {
            BinaryColumn c = new BinaryColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("VarBinary")) {
            VarBinaryColumn c = new VarBinaryColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("LongVarBinary")) {
            LongVarBinaryColumn c = new LongVarBinaryColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("LongVarChar")) {
            LongVarCharColumn c = new LongVarCharColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("VarChar")) {
            VarCharColumn c = new VarCharColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("Char")) {
            CharColumn c = new CharColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("Decimal")) {
            DecimalColumn c = new DecimalColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("Numeric")) {
            NumericColumn c = new NumericColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        } else if (s.equalsIgnoreCase("Other")) {
            OtherColumn c = new OtherColumn(columnName);
            c.setPrimaryKey(primaryKey);
            c.setAllowNulls(allowsNull);
            c.setDefaultValue(defaultValue);
            c.setForeignKey(foreignKey);
            return c;
        }

        return new VarCharColumn(columnName);
    }

    private static String getValue(String key, List<String> keys, List<String> values) {
        for (int x = 0; x < keys.size(); x++) {
            String k = keys.get(x);
            if (k.equalsIgnoreCase(key)) {
                return values.get(x);
            }
        }
        return null;
    }

    public String getColumnName() {
        String name = this.getClass().getSimpleName();
        return name.substring(0, name.length() - "Column".length()).toUpperCase();
    }

    public String getDDL() {
        return getColumnName() + "" + (allowNulls ? " NULL" : " NOT NULL");
    }

    /**
     * Returns the SQL formatted value of the object
     *
     * @param value
     * @return
     */
    public String getFormatted(Object value) {
        return value.toString();
    }

    public String getDataTypeName() {
        String simpleName = this.getClass().getSimpleName();
        int index = simpleName.indexOf("Column");
        if (index > -1) {
            return simpleName.substring(0, index).toUpperCase();
        }
        return simpleName;
    }

    public String getMetaHeader() {
        StringBuilder b = new StringBuilder();
        b.append("type=").append(getDataTypeName());
        if (isPrimaryKey()) {
            b.append(",primaryKey=true");
        }
        if (this instanceof AutoIncrementColumn) {
            AutoIncrementColumn aic = (AutoIncrementColumn) this;
            if (aic.isAutoIncrement()) {
                b.append(",autoIncrement=true");
            }
        }
        if (this instanceof SizeColumn) {
            SizeColumn sc = (SizeColumn) this;
            if (sc.getSize() != Integer.MAX_VALUE) {
                b.append(",size=").append(sc.getSize());
            }

        }
        if (!isAllowNulls()) {
            b.append(",allowNulls=false");
        }
        if (getForeignKey() != null) {
            b.append(",foreignKey=");
            b.append(getForeignKey().getDDL());
        }
        return b.toString();
    }

    public boolean isMySQL() {
        return !isPostgreSQL();
    }

    public boolean isPostgreSQL() {
        return true;
    }

    public abstract void updateResultSet(int columnIndex, Row row, ResultSet rs) throws SQLException;

}
