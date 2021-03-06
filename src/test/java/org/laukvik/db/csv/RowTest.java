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

import org.junit.Test;
import org.laukvik.db.ddl.BinaryColumn;
import org.laukvik.db.ddl.BitColumn;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.FloatColumn;
import org.laukvik.db.ddl.IntegerColumn;
import org.laukvik.db.ddl.VarCharColumn;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class RowTest {

    public RowTest() {
    }

    @Test
    public void shoudAddRow() {
        CSV csv = new CSV();
        Column column = csv.addColumn("column");
        Row r = csv.addRow();
    }

    @Test
    public void shouldSetInteger() {
        CSV csv = new CSV();
        IntegerColumn id = csv.addIntegerColumn(new IntegerColumn("id"));
        Row r = csv.addRow();
        r.update(id, 123);
    }

    @Test
    public void shouldSetFloat() {
        CSV csv = new CSV();
        FloatColumn fc = csv.addFloatColumn(new FloatColumn("id"));
        Row r = csv.addRow();
        r.update(fc, 123.45d);
    }

    @Test
    public void shouldSetString() {
        CSV csv = new CSV();
        VarCharColumn sc = csv.addStringColumn("desc");
        Row r = csv.addRow();
        r.update(sc, "just testing");
    }

    @Test
    public void shouldSetBoolean() {
        CSV csv = new CSV();
        BitColumn bc = csv.addBooleanColumn(new BitColumn("isTrue"));
        Row r = csv.addRow();
        r.update(bc, true);
    }

    @Test
    public void shouldSetByte() {
        CSV csv = new CSV();
        BinaryColumn bc = csv.addByteColumn(new BinaryColumn("byte"));
        Row r = csv.addRow();
        byte a = 2;
        r.update(bc, a);
    }
}
