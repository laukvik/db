package org.laukvik.db.parser.reader;

import org.laukvik.db.parser.Table;

public interface TableListener {

    void found(Table table);
}
