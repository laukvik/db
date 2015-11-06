package org.laukvik.db.csv.sql.parser;

import org.laukvik.db.csv.sql.ParseException;
import org.laukvik.db.csv.sql.Table;
import org.laukvik.db.csv.sql.joins.CrossJoin;

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