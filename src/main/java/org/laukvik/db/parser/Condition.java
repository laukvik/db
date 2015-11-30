package org.laukvik.db.parser;

import org.laukvik.db.jdbc.data.ColumnData;

public abstract class Condition {

    public Condition() {
    }

    public abstract boolean accepts(ColumnData data, String[] values);

}
