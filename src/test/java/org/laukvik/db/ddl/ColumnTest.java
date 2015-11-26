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

import java.text.ParseException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class ColumnTest {

    @Test
    public void autoIncrement() {
        IntegerColumn c1 = (IntegerColumn) Column.parseColumn("Presidency(type=Integer,autoIncrement=true)");
        IntegerColumn c2 = (IntegerColumn) Column.parseColumn("Presidency(type=Integer,autoIncrement=false)");
        Assert.assertEquals("autoIncrement", true, c1.isAutoIncrement());
        Assert.assertEquals("autoIncrement", false, c2.isAutoIncrement());
    }

    @Test(expected = IllegalColumnDefinitionException.class)
    public void autoIncrementFailsIllegal() {
        IntegerColumn c4 = (IntegerColumn) Column.parseColumn("Presidency(type=Integer,autoIncrement=BLAHBLAH)");
    }

    @Test
    public void autoIncrementOkEmpty() {
        IntegerColumn c3 = (IntegerColumn) Column.parseColumn("Presidency(type=Integer,autoIncrement=)");
    }

    @Test
    public void defaultValues() {
        IntegerColumn c = (IntegerColumn) Column.parseColumn("Presidency(type=Integer,default=1)");
        Assert.assertEquals("defaultValue", "1", c.getDefaultValue());
    }

    @Test
    public void foreignKey() {
        IntegerColumn c = (IntegerColumn) Column.parseColumn("Presidency(type=integer,foreignKey=Employee(id))");
        ForeignKey fk1 = new ForeignKey("Employee", "id");
        ForeignKey fk2 = c.getForeignKey();
        Assert.assertEquals("foreignKey", fk1, fk2);
    }

    @Test
    public void parseInteger() {
        IntegerColumn c = (IntegerColumn) Column.parseColumn("Presidency(type=INTEGER,primaryKey=true,increment=true,foreignKey=Employee(id))");
        Assert.assertEquals("Presidency", c.getName());
        Assert.assertEquals("primaryKey", true, c.isPrimaryKey());
    }

    @Test
    public void parseDate() {
        DateColumn c = (DateColumn) Column.parseColumn("Took office(type=Date,format=MM/dd/yyyy)");
        Assert.assertEquals("Took office", c.getName());
        Assert.assertEquals("MM/dd/yyyy", c.getFormat());
    }

    @Test(expected = IllegalColumnDefinitionException.class)
    public void illegalDateFormat1() {
        Column.parseColumn("Took office(type=Date,format=MM/dd/i)");
    }

    @Test(expected = IllegalColumnDefinitionException.class)
    public void illegalDateFormat2() {
        Column.parseColumn("Took office(type=Date,format)");
    }

    @Test
    public void parseString() {
        VarCharColumn c = (VarCharColumn) Column.parseColumn("President(type=VARCHAR,allowNulls=true,primaryKey=true,size=20)");
        Assert.assertEquals("President", c.getName());
        Assert.assertEquals("allowNulls", true, c.isAllowNulls());
        Assert.assertEquals("primaryKey", true, c.isPrimaryKey());
        Assert.assertEquals(20, c.getSize());
    }

    @Test(expected = IllegalColumnDefinitionException.class)
    public void stringSizeShouldFails1() {
        Column.parseColumn("President(type=VARCHAR,size=abcde)");
    }

    @Test(expected = IllegalColumnDefinitionException.class)
    public void stringSizeShouldFails2() {
        Column.parseColumn("President(type=VARCHAR,size=1abc)");
    }

    @Test(expected = IllegalColumnDefinitionException.class)
    public void stringSizeShouldFails3() {
        Column.parseColumn("President(type=VARCHAR,size=abc1)");
    }

    @Test
    public void shouldParseTimeformat() throws ParseException {
        TimestampColumn c = (TimestampColumn) Column.parseColumn("President(type=TIMESTAMP,format=yyyy-DD-mm HH:mm:ss.SSS)");
        Assert.assertEquals("yyyy-DD-mm HH:mm:ss.SSS", c.getFormat());
        Date date = c.getDateFormat().parse("2011-10-13 16:26:45.0");
    }

    @Test
    public void stringSizeShouldNotFail() {
        VarCharColumn c = (VarCharColumn) Column.parseColumn("President(type=VARCHAR,size= 1 )");
        Assert.assertEquals(1, c.getSize());
    }

    @Test
    public void withoutMeta() {
        Column c = Column.parseColumn("President");
        Assert.assertEquals(VarCharColumn.class, c.getClass());
    }

    @Test
    public void testingTypes() {
        BigIntColumn c1 = (BigIntColumn) Column.parseColumn("created(type=BIGINT)");
        BinaryColumn c2 = (BinaryColumn) Column.parseColumn("created(type=BINARY)");
        BitColumn c3 = (BitColumn) Column.parseColumn("created(type=BIT)");
        CharColumn c4 = (CharColumn) Column.parseColumn("created(type=CHAR)");
        DateColumn c5 = (DateColumn) Column.parseColumn("created(type=DATE,format=yyyy.MM.dd)");
        DecimalColumn c6 = (DecimalColumn) Column.parseColumn("created(type=DECIMAL)");
        DoublePrecisionColumn c7 = (DoublePrecisionColumn) Column.parseColumn("created(type=DOUBLE)");

        FloatColumn c8 = (FloatColumn) Column.parseColumn("created(type=FLOAT)");
        LongVarBinaryColumn c9 = (LongVarBinaryColumn) Column.parseColumn("created(type=LONGVARBINARY)");
        LongVarCharColumn c10 = (LongVarCharColumn) Column.parseColumn("created(type=LONGVARCHAR)");
        NumericColumn c11 = (NumericColumn) Column.parseColumn("created(type=NUMERIC)");
        OtherColumn c12 = (OtherColumn) Column.parseColumn("created(type=OTHER)");
        RealColumn c13 = (RealColumn) Column.parseColumn("created(type=REAL)");
        TimeColumn c15 = (TimeColumn) Column.parseColumn("created(type=TIME,format=HH:mm:ss)");
        TimestampColumn c16 = (TimestampColumn) Column.parseColumn("created(type=TIMESTAMP,format=yyyy.MM.dd HH:mm:ss)");

        TinyIntColumn c17 = (TinyIntColumn) Column.parseColumn("created(type=TINYINT)");
        VarBinaryColumn c18 = (VarBinaryColumn) Column.parseColumn("created(type=VARBINARY)");
        VarCharColumn c19 = (VarCharColumn) Column.parseColumn("created(type=VARCHAR)");

    }

}
