package org.laukvik.db.parser.reader;

import org.laukvik.db.parser.Column;

public interface ColumnListener {

	void found(Column column);
	
}