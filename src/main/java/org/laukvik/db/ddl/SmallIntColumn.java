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

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class SmallIntColumn extends Column<Short> {

    public SmallIntColumn(String name) {
        super(name);
    }

    @Override
    public String asString(Short value) {
        return value.toString();
    }

    @Override
    public Short parse(String value) {
        return Short.parseShort(value);
    }

    public int compare(Short one, Short another) {
        return one.compareTo(another);
    }

    @Override
    public int hashCode() {
        int hash = 8;
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
        final SmallIntColumn other = (SmallIntColumn) obj;
        return true;
    }

}
