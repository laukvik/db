package org.laukvik.db.csv.sql.parser;

import org.laukvik.db.csv.sql.Table;

public interface TableListener {

    public void found(Table table);
}
