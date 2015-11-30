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
package org.laukvik.db.sql.swing;

import java.awt.Point;
import java.util.Objects;
import org.laukvik.db.ddl.Table;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class TablePosition {

    Table table;
    Point point;
    boolean selected;

    public TablePosition(Table table, Point point) {
        this.table = table;
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.table);
        hash = 79 * hash + Objects.hashCode(this.point);
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
        final TablePosition other = (TablePosition) obj;
        if (!Objects.equals(this.table, other.table)) {
            return false;
        }
        return Objects.equals(this.point, other.point);
    }

}
