package org.laukvik.db.parser.reader;

import org.laukvik.db.parser.ParseException;
import org.laukvik.db.parser.Table;
import org.laukvik.db.parser.joins.CrossJoin;

public class CrossJoinReader extends JoinReader {

	String table;
	
	public CrossJoinReader() {
		add( new TextReader("CROSS JOIN") );
		addEmpty();
		add( new WordReader() ).addReaderListener( new ReaderListener(){
			public void found(String values) {
				table = values;
			}}  );
	}
	
	public CrossJoin getJoin() throws ParseException{
		return new CrossJoin( null, Table.parse( table ) );
	}

}