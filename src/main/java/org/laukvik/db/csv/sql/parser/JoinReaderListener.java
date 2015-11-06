package org.laukvik.db.csv.sql.parser;

import org.laukvik.db.csv.sql.Join;

public interface JoinReaderListener {

	public void found( Join join );
	
}