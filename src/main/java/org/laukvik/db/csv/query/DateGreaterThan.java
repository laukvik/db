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
package org.laukvik.db.csv.query;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.laukvik.db.csv.Row;
import org.laukvik.db.ddl.DateColumn;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class DateGreaterThan extends RowMatcher {

    Date value;
    SimpleDateFormat format;
    DateColumn column;

    public DateGreaterThan(DateColumn column, Date value, SimpleDateFormat format) {
        super();
        this.column = column;
        this.value = value;
        this.format = format;
    }

    @Override
    public boolean mathes(Row row) {
        Date d = row.getDate(column);
        if (d == null) {
            return false;
        }
        return d.compareTo(value) > 0;

//        if (c instanceof DateColumn){
//            DateColumn dc = (DateColumn)c;
//            String value = row.getDate(columnIndex);
//            Date d = dc.parse(value);
//        }
//
//        Date d = row.getDate(columnIndex, format);
//        return d.compareTo(value) > 0;
    }

}
