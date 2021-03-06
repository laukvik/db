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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.laukvik.db.csv.jdbc.syntax.ColumnReaderTest;
import org.laukvik.db.csv.jdbc.syntax.GroupReaderTest;
import org.laukvik.db.csv.jdbc.syntax.InsertTest;
import org.laukvik.db.csv.jdbc.syntax.ListItemReaderTest;
import org.laukvik.db.csv.jdbc.syntax.ListReaderTest;
import org.laukvik.db.csv.jdbc.syntax.MultipleJoinReaderTest;
import org.laukvik.db.csv.jdbc.syntax.SelectReaderTest;
import org.laukvik.db.csv.jdbc.syntax.StringReaderTest;
import org.laukvik.db.csv.jdbc.syntax.TextReaderTest;
import org.laukvik.db.csv.jdbc.syntax.UpdateTest;
import org.laukvik.db.csv.jdbc.syntax.WhereReaderTest;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
@Suite.SuiteClasses({
    ColumnReaderTest.class,
    GroupReaderTest.class,
    InsertTest.class,
    ListItemReaderTest.class,
    ListReaderTest.class,
    MultipleJoinReaderTest.class,
    SelectReaderTest.class,
    StringReaderTest.class,
    TextReaderTest.class,
    UpdateTest.class,
    WhereReaderTest.class
})
@RunWith(Suite.class)
public class SyntaxSuite {

}
