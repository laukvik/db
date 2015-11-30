package org.laukvik.db.parser.reader;

import org.laukvik.db.parser.Join;

public interface JoinReaderListener {

	void found(Join join);
	
}