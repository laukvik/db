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
public class BitColumn extends Column<Boolean> {

    private String name;

    public BitColumn(String name) {
        super(name);
    }

    @Override
    public String asString(Boolean value) {
        return value.toString();
    }

    @Override
    public Boolean parse(String value) {
        return Boolean.parseBoolean(value);
    }

    public int compare(Boolean one, Boolean another) {
        return one.compareTo(another);
    }

    @Override
    public String toString() {
        return name + "(Boolean)";
    }
}