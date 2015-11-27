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
package org.laukvik.db.csv.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.laukvik.db.csv.CSV;
import org.laukvik.db.csv.Row;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.VarCharColumn;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class CsvReaderTest {

    public CsvReaderTest() {
    }

    @Test
    public void testSomeMethod() throws IOException, Exception {
        File file = File.createTempFile("Person", ".csv");
        // Write
        CSV csv = new CSV();
        VarCharColumn first = csv.addStringColumn("First");
        Column last = csv.addColumn("Last");
        csv.write(new CsvWriter(new FileOutputStream(file), Charset.defaultCharset()));
        // Read it back
        CsvReader r = new CsvReader(new FileInputStream(file));
        Assert.assertEquals(2, r.getMetaData().getColumnCount());
        while (r.hasNext()) {
            Row row = r.next();
        }
    }

}
