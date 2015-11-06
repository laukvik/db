package org.laukvik.db.csv.sql.parser;

import org.laukvik.db.csv.sql.Column;

public interface ColumnListener {

	public void found( Column column );
	
}