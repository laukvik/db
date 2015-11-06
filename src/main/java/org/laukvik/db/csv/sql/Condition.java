package org.laukvik.db.csv.sql;

import org.laukvik.db.csv.jdbc.data.ColumnData;

public abstract class Condition {

    public Condition() {
    }

    public abstract boolean accepts(ColumnData data, String[] values);

}
