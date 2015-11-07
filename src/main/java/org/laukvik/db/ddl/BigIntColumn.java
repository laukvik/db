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
public class BigIntColumn extends Column<Long> implements AutoIncrementColumn {

    private boolean autoIncrement;

    public BigIntColumn(String name) {
        super(name);
    }

    @Override
    public String asString(Long value) {
        return value.toString();
    }

    @Override
    public Long parse(String value) {
        return Long.parseLong(value);
    }

    public int compare(Long one, Long another) {
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
        final BigIntColumn other = (BigIntColumn) obj;
        return true;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

}
